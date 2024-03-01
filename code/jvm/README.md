# Fetcher API Documentation

## Table of Contents

- [Fetcher API Documentation](#fetcher-api-documentation)
  - [Table of Contents](#table-of-contents)
  - [Introduction](#introduction)
  - [Modeling the Database](#modeling-the-database)
    - [Database Schema](#database-schema)
  - [API Endpoints](#api-endpoints)
    - [POST /provider](#post-provider)
    - [DELETE /provider](#delete-provider)
  - [Input Models](#input-models)
    - [Provider Input Model](#provider-input-model)
  - [Output Models](#output-models)
  - [Error Handling](#error-handling)
  - [Testing](#testing)
  - [Running the Application](#running-the-application)

## Introduction

This document outlines the API endpoints and the database schema for the Fetcher API.
The Fetcher API is a simple REST API that allows users to add and remove providers from the database and to make 
requests to the providers to fetch data.
Everytime a provider is added to the database, the API will make a request to the provider's endpoint to fetch the data 
is the provider active.
A Provider is considered active if the request to the provider's endpoint has a field `active` set to `true`.

## Modeling the Database

The following diagram holds the Entity-Relationship diagram for the database schema.

![ER Diagram - Fetcher API](./../../docs/ER_Diagram_fetcher.drawio.png)

### Database Schema

The physical model of the database is available in [create-tables.sql](src/sql/create-tables.sql).

To implement and manage the database, PostgresSQL was used.

The [`code/sql`](src/sql) folder contains all SQL scripts developed:
- [create-tables.sql](src/sql/create-tables.sql): SQL script to create the tables in the database.
- [insert-test-data.sql](src/sql/insert-test-data.sql): SQL script to insert test data into the database.

## API Endpoints

The API has the following endpoints:

### POST /provider
