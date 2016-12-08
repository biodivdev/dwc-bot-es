# dwc-bot-es

A bot to read DarwinCore Archives (DwC-A) from IPTs and index them on ElasticSearch, indexing Resources, Checklists and Occurrences.

## Deploy

You might need to run this first before starting ElasticSearch:

    $ sudo sysctl -w vm.max_map_count=262144

### Run with Docker

Run the docker container

    $ docker run -d -volume /etc/biodiv:/etc/biodiv:ro diogok/dwc-bot-es

Or with docker-compose, including ElasticSearch and Kibana for exploration:

```yaml
version: "2"
services:
  dwc-bot-es:
    image: diogok/dwc-bot-es
    volumes:
      - /etc/biodiv:/etc/biodiv:ro
  elasticsearch:
    image: diogok/elasticsearch
    ports:
      - 9200:9200
    volumes:
      - /var/data/dwc-bot:/usr/share/elasticsearch:rw
  kibana:
    image: diogok/kibana
    ports:
      - 8001:8001
```

### Run the JAR

Download the latest jar from the [ realases page ](https://github.com/diogok/dwc-bot-es/releases) and run it:

    $ java -jar dwc-bot-es.jar

### Configuration

It will look for a configuration file on /etc/biodiv/config.ini or at the file defined by CONFIG environment variable.

The configuration file looks like the following:

    ELASTICSEARCH=http://localhost:9200
    INDEX=dwc
    LOOP=false

ElasticSearch tells to which elasticsearch server to connect. INDEX tells which ElasticSearch index to use. LOOP defines if the it should run in loop(true) or only once(false).

It will also look for a list of IPTs to crawl in /etc/biodiv/dwc-bot.list or at the file defined by DWC\_BOT environment variable.

You can set the ElasticSearch and Index to use with environment variables, such as:

    $ CONFIG=/etc/biodiv/config.ini DWC_BOT=/etc/biodiv/dwc-bot.list ELASTICSEARCH=http://localhost:9200 INDEX=dwc java -jar dwc-bot-es.jar

Or to run a single(or any) source(s):

    $ ELASTICSEARCH=http://localhost:9200 INDEX=dwc java -jar dwc-bot-es.jar http://ipt.jbrj.gov.br/jbrj/resource?r=lista_especies_flora_brasil

The environment variable LOOP controls if the bot should run only once or keep running:

    $ LOOP=true java -jar dwc-bot-es.jar

Or all options combined:

    $ LOOP=true DWC_BOT=/etc/biodiv/dwc-bot.list ELASTICSEARCH=http://localhost:9200 INDEX=dwc java -jar dwc-bot-es.jar

If not running on a system with environment variables you can also set them using java properties, as such:

    $ java -jar -DLOOP=true -DDWC_BOT=/etc/biodiv/dwc-bot.list -DELASTICSEARCH=http://localhost:9200 -DINDEX=dwc dwc-bot-es.jar

## Development

Start the elasticsearch local server with docker-compose:

    $ docker-compose up

Using leningen, the tasks are:

    $ lein run # to run the bot once
    $ lein uberjar # generate the deploy artifact
    $ docker build -t dwc-bot . # build the docker image

If not using the docker-compose elasticsearch and not at localhost:9200, set the proper environment variable:

    $ ELASTICSEARCH=http://elasticsearch:9200 lein run

## License

MIT

