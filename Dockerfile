FROM diogok/java8:zulu

WORKDIR /opt
CMD ["java","-server","-XX:+UseConcMarkSweepGC","-XX:+UseCompressedOops","-XX:+DoEscapeAnalysis","-jar","dwc-bot-es.jar"]

ADD target/dwc-bot-es-0.0.5-standalone.jar /opt/dwc-bot-es.jar

