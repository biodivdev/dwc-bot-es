# dwc-bot-es

A bot to read DarwinCore Archives from IPTs and index them on ElasticSearch, indexing Resources, Checklists and Occurrences.

## Deploy

### Run with Docker

Run the docker container

    $ docker run -d -volume /etc/biodiv:/etc/biodiv:ro diogok/dwc-bot-es

With docker-compose, including ElasticSearch and Kibana for exploration:

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

    $ java -server -jar dwc-bot-es.jar

### Configuration

It will look for a list of IPTs to crawl in /etc/biodiv/dwc-bot.list or at the directory defined by DWC\_BOT environment variable.

You can set the ElasticSearch and Index to use with environment variables, such as:

    $ DWC_BOT=/etc/biodiv/dwc-bot.list ELASTICSEARCH=http://localhost:9200 IDNEX=dwc java -jar dwc-bot-es.jar

Or to run a single(or any) source(s):

    $ ELASTICSEARCH=http://localhost:9200 IDNEX=dwc java -jar dwc-bot-es.jar http://ipt.jbrj.gov.br/jbrj/resource?r=lista_especies_flora_brasil

The environment variable LOOP controls if the bot should run only once or keep running:

    $ LOOP=true java -jar dwc-bot-es.jar

Or all options combined:

    $ LOOP=true DWC_BOT=/etc/biodiv/dwc-bot.list ELASTICSEARCH=http://localhost:9200 IDNEX=dwc java -jar dwc-bot-es.jar

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

