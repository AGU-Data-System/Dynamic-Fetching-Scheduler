# Fetcher API Documentation

## Table of Contents

- [Introduction](#introduction)
- [Pagination](#pagination)
- [API Endpoints](#api-endpoints)
    - [POST api/provider](#post-apiprovider)
    - [POST api/provider/update](#post-apiproviderupdate)
    - [DELETE api/provider](#delete-apiprovider)
    - [GET api/providers](#get-apiproviders)
    - [GET api/provider](#get-apiprovider)
- [Input Models](#input-models)
    - [Provider Input Model](#provider-input-model)
- [Output Models](#output-models)
    - [Get Provider Output Model](#get-provider-output-model)
    - [Provider Raw Data Output Model](#provider-raw-data-output-model)
    - [Provider With Data Output Model](#provider-with-data-output-model)
    - [List Provider With Data Output Model](#list-provider-with-data-output-model)
    - [Simple Message](#simple-message)
- [Error Handling](#error-handling)

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
            - [Get Provider Output Model](#get-provider-output-model)
- **Error Response:**
    - **Content:** 
        - `application/json`
            - [Bad Request](#bad-request)
- **Sample Call:**
    ```shell
    curl -X POST "http://localhost:8080/api/provider" -H "Content-Type: application/json" -d '{"name": "provider1", "endpoint": "http://localhost:8080/provider1", "active": true}'
    ```
  
### POST api/provider/update

Updates a provider if it exists.

- **URL:** `/api/provider/update`
- **Method:** `POST`
- **Request Body:**
    - **Required:**
        - [Provider Input Model](#provider-input-model)
- **Success Response:**
    - **Content:** 
        - `application/json`    
            - [Get Provider Output Model](#get-provider-output-model)
- **Error Response:**
    - **Content:** 
        - `application/json`
            - [Bad Request](#bad-request)
- **Sample Call:**
    ```shell
    curl -X POST "http://localhost:8080/api/provider/update" -H "Content-Type: application/json" -d '{"name": "provider1", "endpoint": "http://localhost:8080/provider1", "active": true}'
    ```

### DELETE api/provider

Deletes a provider.

- **URL:** `/api/provider`
- **Method:** `DELETE`
- **Request Body:**
    - **Required:**
        - `url` - The unique url of the provider to be deleted.
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
    curl -X DELETE "http://localhost:8080/api/provider" -H "Content-Type: application/json" -d '{"url": "http://localhost:8080/provider1"}'
    ```

### GET api/providers

Fetches all providers and their data.

- **URL:** `/api/providers`
- **Method:** `GET`
- **Request Body:**
    - **Required:**
        - `beginDate` - The begin date of the period to fetch the data. // TODO doc type and format
        - `endDate` - The end date of the period to fetch the data.
    - **Optional:**
        - `page` - The page number to fetch the data. // TODO doc type and format
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
      curl -X GET "http://localhost:8080/api/providers?beginDate=2021-01-01&endDate=2021-01-31&page=1&size=10"
    ```
  
### GET api/provider

Fetches a provider and its data.

- **URL:** `/api/provider`
- **Method:** `GET`
- **Request Body:**
    - **Required:**
        - `url` - The unique url of the provider to be fetched.
        - `beginDate` - The begin date of the period to fetch the data. // TODO doc type and format
        - `endDate` - The end date of the period to fetch the data.
    - **Optional:**
        - `page` - The page number to fetch the data. // TODO doc type and format
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
        - `endpoint` - The endpoint of the provider.
        - `active` - The status of the provider.
    - **Optional:**
        - `id` - The unique id of the provider.
- **Example:**
- ```json
  {
    "name": "provider1",
    "endpoint": "http://localhost:8080/provider1",
    "active": true
  }
  ```
  
## Output Models

### Get Provider Output Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `name` - The name of the provider.
        - `endpoint` - The endpoint of the provider.
        - `active` - The status of the provider.
    - **Optional:**
        - `id` - The unique id of the provider.
- **Example:**
- ```json
  {
    "name": "provider1",
    "endpoint": "http://localhost:8080/provider1",
    "active": true
  }
  ```
  
### Simple Message

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `message` - The message.
- **Example:**
- ```json
  {
    "message": "Provider deleted successfully."
  }
  ```
  
### Provider With Data Output Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `name` - The name of the provider.
        - `endpoint` - The endpoint of the provider.
        - `active` - The status of the provider.
        - `data` - The data fetched from the provider.
    - **Optional:**
        - `id` - The unique id of the provider.
- **Example:**
- ```json
  {
    "name": "provider1",
    "endpoint": "http://localhost:8080/provider1",
    "active": true,
    "data": [
      {
        "id": 1,
        "name": "data1",
        "value": "value1"
      },
      {
        "id": 2,
        "name": "data2",
        "value": "value2"
      }
    ]
  }
  ```
  
## Error Handling

### Bad Request

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `message` - The message.
- **Example:**
    ```json
    {
        "message": "Bad request."
    }
  ```
  
Currently, the API only returns a `Bad Request` error message, and there's no other error handling implemented.
