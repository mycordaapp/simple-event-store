# Simple Event Store (ses)

[![Circle CI](https://circleci.com/gh/mycordaapp/simple-event-store.svg?style=shield)](https://circleci.com/gh/mycordaapp/simple-event-store)
[![Licence Status](https://img.shields.io/github/license/mycordaapp/simple-event-store)](https://github.com/mycordaapp/simple-event-store/blob/master/licence.txt)
[![Maintainability](https://api.codeclimate.com/v1/badges/a50b7a770d8644d3bc51/maintainability)](https://codeclimate.com/github/mycordaapp/simple-event-store/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/a50b7a770d8644d3bc51/test_coverage)](https://codeclimate.com/github/mycordaapp/simple-event-store/test_coverage)

## What it does

Simple Event Store is just a minimalist implementation of an event store for the
[Event Sourcing](https://martinfowler.com/eaaDev/EventSourcing.html) pattern.

Two implementations are provided:

* `SimpleEventStore` works in memory and is only intended for use within unit tests and examples.
* `FileEventStore` persists to file system. It is not intended for production usage.

The anticipation is that other implementations will be provided for production, for example a `JpaEventStore` that is
backed by a relational database. To support this the units will be refactored into a suite of common tests that should
pass for any event store.

There are two basic restrictions on an individual event:

* the event `payload` (i.e. tha actual data) must conform to rules
  of [Really Simple Serialisation(rss)](https://github.com/mycordaapp/really-simple-serialisation#readme).
* there is a limit of on size of an event, current set to 32K for the payload in its serialised json format. This is for
  query efficiency; the event must be small enough to fit with size limit of single row for database - there is no
  industry agreed standard here, but in practice all the main stream database allow at least 64K in their current
  releases so 32KB feels a reasonable limit, allowing plenty of room for other columns in the row.
  
## Dependencies

As with everything in [myCorda dot App](https://mycorda.app), this library has minimal dependencies.

* Kotlin 1.4
* Java 11
* The object [Registry](https://github.com/mycordaapp/registry#readme)
* The [Really Simple Serialisation(rss)](https://github.com/mycordaapp/really-simple-serialisation#readme) module
    - [Jackson](https://github.com/FasterXML/jackson) for JSON serialisation

## Next Steps 

* [Using](docs/event-store.md) the event store.
* [Managing Consistency](docs/event-consistency.md) when writing events.