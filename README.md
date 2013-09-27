# Deckchair

A lightweight clientside JSON document store modeled after [Lawnchair](https://github.com/brianleroux/lawnchair).

It is meant to simulate a database for demo applications that require only a small amount of data where it would be overkill to use a traditional database.

Deckchair uses adaptors to implement the actual database functions like its inspiration Lawnchair. At the moment, the only implemented adaptor is ApacheDB(Derby).

## Installation

Include the deckchair jar, Apache DB and the org.json jar on the path of your app.

## Functions

all()			Retrieve a collection of all records
save([:])		Save a record
get(String)		Retrieve a record by its UUID
find(Closure)		Find records that satisfy a condition
remove(String)		Remove a record by passing the object key
remove(Object)		Remove a record by passing the object
nuke()			Removes all records from the database

Functions show above with their required properties. Every function except _remove_ and _nuke_ can be passed a closure to act on the elements that is executed after the primary action is completed.

## Implemented Adaptors
ApacheDB(Derby)

# Planned Adaptors
Flat File


