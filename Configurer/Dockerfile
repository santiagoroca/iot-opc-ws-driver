FROM openjdk:8-jre

#
ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/opc-configurer/service.jar"]

# Add the service itself
ADD target/youbim-opc-ws-configurer-0.1.0.jar /usr/share/opc-configurer/service.jar