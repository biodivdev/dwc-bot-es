# dwc-bot-es

A bot to read DarwinCore Archives from IPTs and index them on ElasticSearch.

## Deploy

With docker (recommended):

    $ docker run -d -p 8383:8383 -volume /var/data/dwc-bot:/var/data/dwc-bot:rw diogok/dwc-bot

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
      - /var/data/dwc-elasticsearch/data:/usr/share/elasticsearch/data:rw
  kibana:
    image: diogok/kibana
    ports:
      - 8001:8001
```

Manual:

Download the latest jar from the [ realases page ](https://github.com/diogok/dwc-bot-es/releases) and run it:

    $ java -server -jar dwc-bot-es.jar

For all:

It will look for a list of IPTs to crawl in /etc/biodiv/dwc-bot.list or at DWC\_BOT env var.

You can set the ElasticSearch and Index to use with env vars, such as:

    $ DWC_BOT=/etc/biodiv/dwc-bot.list ELASTICSEARCH=http://localhost:9200 IDNEX=dwc java -jar dwc-bot-es.jar

## Dev

Install leningen, the tasks are:

    $ lein run # to run the server, with code reload
    $ lein uberjar # generate the deploy artifact
    $ docker build -t dwc-bot . # build the docker image

## License

MIT

