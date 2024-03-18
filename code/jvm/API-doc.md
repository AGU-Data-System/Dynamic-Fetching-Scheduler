# Dynamic Fetching Scheduler — API Documentation

## Table of Contents

- [Introduction](#introduction)
- [Pagination](#pagination)
- [Date and Time format](#date-and-time-format)
- [API Endpoints](#api-endpoints)
    - [POST api/provider](#post-apiprovider)
    - [POST api/provider/{id}](#post-apiproviderid)
    - [DELETE api/provider](#delete-apiproviderid)
    - [GET api/providers](#get-apiproviders)
    - [GET api/provider](#get-apiprovider)
- [Input Models](#input-models)
    - [Provider Input Model](#provider-input-model)
- [Output Models](#output-models)
    - [Simple Message](#simple-message)
    - [Provider Output Model](#provider-output-model)
    - [Provider Raw Data Output Model](#provider-raw-data-output-model)
    - [Provider With Data Output Model](#provider-with-data-output-model)
    - [Provider List Output Model](#provider-list-output-model)
- [Error Handling](#error-handling)
    - [Bad Request](#bad-request)

## Introduction

This document outlines the API endpoints and how to make requests to say API.
The Fetcher API is a simple REST API that allows users to add and remove providers from the database and to make
requests to the providers to fetch data.
Everytime a provider is added to the database, the API will make a request to the provider's endpoint to fetch the data
is the provider active.
A Provider is considered active if the request to the provider's endpoint has a field `active` set to `true`.

## Pagination

The API uses pagination to limit the number of results returned in a single request.
When pagination is used, each page has a maximum of XX results.


## Date and Time format

This API is designed to only use the [ISO-8601](https://en.wikipedia.org/wiki/ISO_8601#Durations) format for date and 
time.

## API Endpoints

The API has the following endpoints:

### POST api/provider

Adds a new provider.

- **URL:** `/api/provider`
- **Method:** `POST`
- **Request Body:**
    - **Required:**
        - [Provider Input Model](#provider-input-model)
- **Success Response:**
    - **Content:** 
        - `application/json`    
            - [Provider Output Model](#provider-output-model)
- **Error Response:**
    - **Content:** 
        - `application/json`
            - [Bad Request](#bad-request)
- **Sample Call:**
    ```shell
    curl -X POST "http://localhost:8080/api/provider" -H "Content-Type: application/json" -d '{"name": "provider1", "url": "http://localhost:8080/provider1", "frequency": "PT20S", "isActive": "true"}'
    ```
  
### POST api/provider/{id}

Updates a provider if it exists.

- **URL:** `/api/provider/{id}`
- **Method:** `POST`
- **Path Variables:**
    - **Required:**
        - `id` - The unique id of the provider to be updated.
- **Request Body:**
    - **Required:**
        - [Provider Input Model](#provider-input-model)
- **Success Response:**
    - **Content:** 
        - `application/json`    
            - [Provider Output Model](#provider-output-model)
- **Error Response:**
    - **Content:** 
        - `application/json`
            - [Bad Request](#bad-request)
- **Sample Call:**
    ```shell
    curl -X POST "http://localhost:8080/api/provider/1" -H "Content-Type: application/json" -d '{"name": "provider2", "url": "http://localhost:8080/provider1", "frequency": "PT20S", "isActive": "true"}'
    ```

### DELETE api/provider/{id}

Deletes a provider.

- **URL:** `/api/provider/{id}`
- **Method:** `DELETE`
- **Path Variables:**
    - **Required:**
        - `id` - The unique id of the provider to be deleted.
- **Success Response:**
    - **Content:** 
        - `application/json`    
            - [Simple message](#simple-message) // TODO: Change or doc this
- **Error Response:**
    - **Content:** 
        - `application/json`
            - [Bad Request](#bad-request)
- **Sample Call:**
    ```shell
    curl -X DELETE "http://localhost:8080/api/provider/1"
    ```

### GET api/providers

Fetches all providers.

- **URL:** `/api/providers`
- **Method:** `GET`
- **Success Response:**
- **Content:** 
    - `application/json`    
        - [Provider List Output Model](#provider-list-output-model)
- **Error Response:**
    - **Content:** 
        - `application/json`
            - [Bad Request](#bad-request)
- **Sample Call:**
    ```shell
      curl -X GET "http://localhost:8080/api/providers"
    ```
  
### GET api/provider

Fetches a provider and its data for a given period.

- **URL:** `/api/provider`
- **Method:** `GET`
- **Path Variables:**
    - `id` - The unique id of the provider.
- **Request Parameters:**
    - **Required:**
        - `beginDate` - The beginning date of the period to fetch the data.
    - **Optional:**
        - `endDate` - The end date of the period to fetch the data.
        - `page` - The page number to fetch the data.
        - `size` - The number of results to fetch in a single page.
- **Success Response:**
- **Content:** 
    - `application/json`    
        - [Provider With Data Output Model](#provider-with-data-output-model)
- **Error Response:**
    - **Content:** 
         - `application/json`
                - [Bad Request](#bad-request)
- **Sample Call:**
    ```shell
      curl -X GET "http://localhost:8080/api/provider?beginDate=2021-01-01&endDate=2021-01-31&page=1&size=10"
    ```
  
## Input Models

### Provider Input Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `name` - The name of the provider.
        - `url` - The endpoint of the provider to fetch the data from.
        - `frequency` - The frequency to fetch the data from the provider.
        - `isActive` - The status of the provider.
- **Example:**
- ```json
  {
    "name": "provider1",
    "url": "http://localhost:8080/provider1",
    "frequency": "PT20S",
    "isActive": true
  }
  ```
  
## Output Models

### Simple Message

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `message` - The message.
- **Example:**
- ```json
  {
    "message": "Provider with id: 123 deleted successfully."
  }
  ```
  
### Provider Output Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `id` - The unique id of the provider.
        - `name` - The name of the provider.
        - `url` - The endpoint of the provider to fetch data from.
        - `isActive` - The status of the provider.
        - `frequency` - The frequency to fetch the data from the provider.
        - `isActive` - The status of the provider.
        - `lastFetch` - The last time the data was fetched from the provider. If it's null, no data was fetched yet.
- **Example:**
- ```json
  {
    "id": 1,
    "name": "provider1",
    "url": "http://localhost:8080/provider1",
    "frequency": "PT20S",
    "isActive": true,
    "lastFetch": "2024-03-18T12:08:16.494065" 
  }
  ```
  
### Provider Raw Data Output Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `fetchTime` - The time the data was fetched from the provider.
        - `data` - The data fetched from the provider.
- **Example:**
- ```json
  {
    "fetchTime": "2024-03-18T12:05:36.908995",
    "data": "test data"
  }
  ```
  
### Provider With Data Output Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `id` - The unique id of the provider.
        - `name` - The name of the provider.
        - `url` - The endpoint of the provider.
        - `frequency` - The frequency to fetch the data from the provider.
        - `isActive` - The status of the provider.
        - `lastFetch` - The last time the data was fetched from the provider. If it's null, no data was fetched yet.
        - `dataList` - [Provider Raw Data OutputModel](#provider-raw-data-output-model) list.
- **Example:**
- ```json
  {
    "id": 1,
    "name": "provider1",
    "url": "http://localhost:8080/provider1",
    "frequency": "PT20S",
    "isActive": true,
    "lastFetch": "2024-03-18T12:08:16.494065",
    "dataList": [
        {
            "fetchTime": "2024-03-18T12:05:36.908995",
            "data": "test data"
        },
        {
            "fetchTime": "2024-03-18T12:05:56.908995",
            "data": "test data"
        }
    ]
  }
  ```

### Provider List Output Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `providers` - [Provider Output Model](#provider-output-model) list.
        - `size` - The number of results in the list.
- **Example:**
- ```json
  {
    "providers": [
        {
            "id": 1,
            "name": "provider1",
            "url": "http://localhost:8080/provider1",
            "frequency": "PT20S",
            "isActive": true,
            "lastFetch": "2024-03-18T12:20:16.490422"
        },
        {
            "id": 2,
            "name": "provider2",
            "url": "http://localhost:8080/provider2",
            "frequency": "PT20S",
            "isActive": true,
            "lastFetch": "2024-03-18T12:20:30.383328"
        },
        {
            "id": 3,
            "name": "provider1",
            "url": "http://localhost:8080/provider1",
            "frequency": "PT20S",
            "isActive": true,
            "lastFetch": "2024-03-18T12:20:30.383328"
        }
    ],
    "size": 3
  }
  ```

## Error Handling

### Bad Request

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `timestamp` - The time the error occurred.
        - `status` - The status of the error.
        - `error` - The error message.
        - `path` - The path of the request that caused the error.
- **Example:**
- ```json
  {
    "timestamp": "2024-03-18T12:28:34.971+00:00",
    "status": 400,
    "error": "Bad Request",
    "path": "/api/provider/a"
  }
  ```
  
Currently, the API only returns a `Bad Request` error message, and there's no other error handling implemented.
