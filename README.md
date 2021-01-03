# EZventory
An Inventory management system for small organizations!

## Project Description
Originally designed for my former high school's Student Council, EZventory has all the features you need to keep track of a small organization's inventory. Some of the most interesting features: Save data to the cloud (AWS) and acess it across any android device, export data to a CSV for easy editing in Excel/Google Sheets, and in-app sorting of items based on price or storage location. Communicates with a [backend](https://github.com/DinuWije/InventoryAppBackend) to store data in a MySQL database. 

[Download the app onto your Android device from the Google Play Store!](https://play.google.com/store/apps/details?id=com.dinuw.firstapp)

# EZventory Backend

REST API Backend for [EZventory app](https://github.com/DinuWije/EZventory), includes a web server created with Flask and a MYSQL Database managed with SQLAlchemy. Authentication of users is handled with tokens and communication with the frontend is done via HTTP requests (initally tested on Postman).

The database and web server were containerized with Docker before and are currently hosted on an AWS EC2 instance.
