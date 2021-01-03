# EZventory
An Inventory management app for small organizations!

## Frontend Description
Originally designed for my former high school's Student Council, EZventory has all the features you need to keep track of a small organization's inventory. The forntend features include saving data to the cloud (AWS) and accessing it across any android device, exporting data to a CSV for easy editing in Excel/Google Sheets, in-app sorting of items based on price or storage location, CRUD operations on inventory items, and the ability to take pictures of items. The app communicates with a RESTful backend to store data in a MySQL database. The frontend was developed on Android Studio using Kotlin.

[Download the app onto your Android device from the Google Play Store!](https://play.google.com/store/apps/details?id=com.dinuw.firstapp)

## Backend Description

The REST API Backend includes a web server created with Python/Flask and a MYSQL Database managed with SQLAlchemy. Authentication of users is handled with tokens and communication with the frontend is done via HTTP requests (initally tested on Postman).

The database and web server were containerized with Docker and are currently hosted on an AWS EC2 instance.
