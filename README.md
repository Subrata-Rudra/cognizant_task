# Music Streaming Service
## Case Study No. - 119
## Submitted By - Subrata Rudra
## Candidate ID - 30743697
## Superset ID - 5044753
## SQL Tables:
1. User Table
2. Song Table
3. Playlist Table
4. Playlist-Song Table
## Table Details
### 1. User Table
user_id(primary key)
<br/>
username
<br/>
email
<br/>
password_hash
### 2. Song Table
song_id(primary key)
<br/>
title
<br/>
artist
<br/>
duration
<br/>
genre
### 3. Playlist Table
playlist_id(primary key)
<br/>
name
<br/>
creator_id(foreign key references User table's user_id)
<br/>
creation_date
### 4. Playlist-Song Table
playlist_id(foreign key references Playlist table's playlist_id)
<br/>
song_id(foreign key references Song table's song_id)
## Features
### 1. User Management
a) Register a new user account
<br/>
b) View user account details
<br/>
c) Update user account information
<br/>
d) Delete a user account
### 2. Song Management
a) Add a new song
b) View song details
c) Update song information
d) Delete a song
### 3. Playlist Management
a) Create a new playlist
b) View playlist details
c) Update playlist information
d) Delete a playlist

## Run The Program
1. Go inside the folder **client**
2. then run the file named **App.java**
3. Then it will show many options from 0 to 12
4. Now enter any number of your choice to use that respective service
