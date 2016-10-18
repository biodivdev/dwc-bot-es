# dwc-bot-es

A bot to read DarwinCore Archives from IPTs and index them on ElasticSearch, indexing Resources, Checklists and Occurrences.

## Deploy

### Run with Docker

Run the docker container

    $ docker run -d -volume /var/data/dwc-bot:/var/data/dwc-bot:rw diogok/dwc-bot-es

With docker-compose:

```yaml
version: "2"
services:
  dwc-bot-es:
    image: diogok/dwc-bot-es
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

It will look for a list of IPTs to crawl in /etc/biodiv/dwc-bot.list or at DWC\_BOT env var.

You can set the ElasticSearch and Index to use with env vars, such as:

    $ DWC_BOT=/etc/biodiv/dwc-bot.list ELASTICSEARCH=http://localhost:9200 IDNEX=dwc java -jar dwc-bot-es.jar

## Dev

Start the elasticsearch local server with docker-compose:



Install leningen, the tasks are:

    $ lein run # to run the bot once
    $ lein uberjar # generate the deploy artifact
    $ docker build -t dwc-bot . # build the docker image

If not using the docker-compose elasticsearch and not at localhost:9200, set the proper environment variable:

    $ ELASTICSEARCH=http://elasticsearch:9200 lein run

## License

MIT

