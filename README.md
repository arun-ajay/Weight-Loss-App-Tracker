# Weight Loss App Tracker

- Track user’s weight loss period
- Display statistics regarding their overall trend
- Allow users to set a goal weight and deadline
- Display a chart of weight loss over “X” amount of entries
- Track user’s exercise activity
- Give visual indicators if user is behind or following estimated weight loss pace/exercise routine. 


## Data Storage

- Data Save/Retrieval Method
  - SQLite Database
    - Used for the storage of log entries and goal row. 
    - Structure:
      - Log_Type TEXT, Date Text PRIMARY KEY, Weight REAL, Time_Spent INTEGER
  - A helper class called “DataBaseAssist”
     - Contains a collection of functions that allows for the retrieval or insertion of rows.
        - Implemented even further by providing certain data in a specific data structure (ArrayLists) or just as raw values after some calculations performed
        - Data is called on a row-by-row basis in SQL and stored into an arraylist of Strings or Floats for calculations or info to be passed back to MainActivity.java for chart plotting

- Data Structures Used
  - ArrayList<String>, ArrayList<Float>

## Authors
- **Arun Ajay** - [@arun-ajay](https://github.com/arun-ajay)

See also the list of [contributors](https://github.com/NYPL/NYPL-Tenable-Jira/graphs/contributors) who participated in this project.


## License
This project is licensed under the MIT License - see the [LICENSE](https://github.com/arun-ajay/Weight-Loss-App-Tracker/blob/master/LICENSE) file for details

