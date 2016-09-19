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

## Dev

Install leningen, the tasks are:

    $ lein run # to run the server, with code reload
    $ lein midje # for tests
    $ lein uberjar # generate the deploy artifact
    $ docker build -t dwc-bot . # build the docker image

## Some numbers

Final run in SQLite with Fulltext Search:

- 113 resources (circa 635MB zip)
- 490000+ occurrences
- 30 minutes
- 8.7 GB

Concluding:

- 1.8KB per occurrence
- 2.700 occurrences per second
- 15x the size of the DarwinCore Zip
- 1.5x the size of the DarwinCore CSV

## License

MIT

