/*
* Wraps the server side node to provide an interface
* for subscription.
*/
function OPCNode (connection, node, building_id) {

    /*
    * Store all the node attributes as first class 
    * attributes
    */
    Object.assign(this, node);

    /*
     * Web Socket Connection to the server
     * with the current building 
     */
    this.connection = connection;

    /*
    * All node information needed to stablish a 
    * subscription
    */
    this.sub_chain = {
        buildingId: building_id, 
        externalId: node.external_id 
    };

    /*
    * Whenever a new message arrives for the current
    * node, the onmessage method gets called. Can be
    * overwritten.
    */
    this.onmessage = function (data) {}
}

/*
* Subscribe the current node to the server
* to receive updates on the data.
*/
OPCNode.prototype.subscribe = function (callback) {
    this.connection.send(JSON.stringify(this.sub_chain));
    this.onmessage = callback;
}

/*
*
*/
function OPCWSClient (building_id, driver_url, pool_url) {

    /*
    * Will contain the full node list, received in the
    * configuration object
    */
    this.nodes = {};

    /*
    * Event listeners for the OPC Client Object
    */
    this.listeners = {};

    /*
    * Executes the connection with the server.
    */
    this.connect(building_id, driver_url, pool_url);
}

/*
* Fetch configurations from the server. Configurations contains the target OCP Server
* URL and the node list.
*/
OPCWSClient.prototype.connect = async function  (building_id, driver_url, pool_url) {

    /*
    * Fetchs the configuration of the current OPC Client (Server) from the Configuration server
    * opc_server_url [String]
    * nodes [Node,...]
    */ 
    this.configurations = await (await fetch(`${pool_url}/configuration/${building_id}`)).json();

    /*
    * Stablish the connection with the WS server
    * which will send the value updates
    */
    this.connection = new WebSocket(driver_url);

    /*
    * On message the client will check the external id and send that information
    * to that particular node's callback
    */
    this.connection.onmessage = data => this.onmessage(JSON.parse(data.data));

    /*
    * On open, initializes all the Node with the Node Object
    * to provide the subscribe method, and the callback for 
    * value udpates.
    */
    this.connection.onopen = () => {
        this.configurations.nodes = this.configurations.nodes.map(node => {
            node = new OPCNode(this.connection, node, building_id);
            this.nodes[node.external_id] = node;
            return node;
        });

        /*
        * Notifies that the nodes are ready to be used.
        */
        this.listeners['nodesready'](this.configurations.nodes);
    }
}

/*
* On message, sends the values to the corresponding Node
*/
OPCWSClient.prototype.onmessage = function (data) {
    this.nodes[data.node.external_id].onmessage(data.data_value);
}

/*
* Fetch configurations from the server. Configurations contains the target OCP Server
* URL and the node list.
*/
OPCWSClient.prototype.onNodeListAvailable = function  (callback) {
    this.listeners['nodesready'] = callback;
}