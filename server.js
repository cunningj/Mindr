var express = require('express')
var app = express();
var mysql      = require('mysql');
var connection = mysql.createConnection({
  host     : 'localhost',
  user     : 'root',
  password : 'basset',
  multipleStatements: true
});

var bodyParser = require('body-parser')
app.use(bodyParser.json())

connection.connect(function(err) {
  if (err) {
    console.error('error connecting: ' + err.stack);
    return;
  }
  console.log('connected as id ' + connection.threadId);
})

// this route gets all the information to display list names on MainActivity
app.get('/api/listPrefs', (req, res) => {
  connection.query('SELECT listName FROM remindr.list_prefs WHERE listName IS NOT NULL',function(err,rows){
    if(err) throw err;
    res.json(rows.map(row => row.listName))
  })
})

// this route gets all of the current location names in the DB on AddListActivity
app.get('/api/locations',(req, res) => {
  connection.query('SELECT locationName FROM remindr.list_prefs WHERE locationName IS NOT NULL', function(err, rows){
  if(err) throw err;
  res.json(rows.map(row => row.locationName))

  })
})

// this route displays list items once a list is clicked on the main activity
// it is a POST route because we have to send from frontend the info about what listName was clicked
app.post('/api/listItems', (req, res) => {
  connection.query(
  `SELECT i.item FROM remindr.list_items as i, remindr.list_prefs as p where p.listName ="${req.body.listName}" and i.listID=p.listID`,
   function(err,rows) {
    if(err) throw err;
    res.json(rows.map(row => row.item))
  })
})

// this route adds a list to the database from the AddListActivity
app.post('/api/addList', (req, res) => {

  var re = /item*/;
  var itemsArr = [];
  var sql =  "";
  var sqlTwo = "";
  // nothing is a joke
  var nothing= [];

// this function makes itemsArr an array of arrays with a single item in each (that is syntax required by SQL)
  for(key in req.body){
    if (key.match(re) && req.body[key]){
      var newArr = [];
      newArr.push(req.body[key]);
      itemsArr.push(newArr);
    }
  }
  
// This is the connection query for when a location name is already set (most probably case)
  if (connection.query(`SELECT locationName FROM remindr.list_prefs WHERE locationName="${req.body.locationName}"`)){

    connection.query(`SELECT listID FROM remindr.list_prefs WHERE locationName="${req.body.locationName}"`, 
      function(err, rows){
        if(err) throw err;
        //Nothing is the id array 
        nothing = rows.map(row => row.listID)
        // res.json(rows.map(row => row.item))
        for (var i = 0; i<itemsArr.length; i++) {
          sqlTwo = sqlTwo + "INSERT INTO remindr.list_items (listID, item) VALUES ('" + nothing[0] + "', '" + itemsArr[i] + "'); \n"
        }

        connection.query(`UPDATE remindr.list_prefs SET listName = "${req.body.listName}", approaching = "${req.body.approaching}" WHERE locationName="${req.body.locationName}";
          ${sqlTwo}`,
          function(err,rows) {
          if(err) throw err;
          res.json(["success"])
          }
        )    
      }
    )
  }

  // we will use this else statement only if we get around to being able to add locations from the Add List page
  // else {  // we don't really expect this to run
  //   console.log("THIS SHOULDNT BE RUNNING")
  //   for (var i = 0; i<itemsArr.length; i++) {
  //     sql = sql + "INSERT INTO remindr.list_items (listID, item) VALUES (LAST_INSERT_ID(), '" + itemsArr[i] + "'); \n"
  //   }

  //   connection.query(
  //   `INSERT INTO remindr.list_prefs (listName, approaching, alertRange, locationName) VALUES ("${req.body.listName}","${req.body.approaching}","${req.body.alertRange}","${req.body.locationName}");
  //   SELECT LAST_INSERT_ID();
  //   ${sql}`,

  //    function(err,rows) {
  //     if(err) throw err;
  //     res.json(["success"])

  //    }
  //   )
  // }
})

app.post('/api/addLocation', (req, res) => {
  connection.query(
  `INSERT INTO remindr.list_prefs (locationName, latitude, longitude) VALUES ("${req.body.locationName}","${req.body.latitude}","${req.body.longitude}")`,
   function(err,rows) {
    if(err) throw err;
    res.json(["success"])
  })
})

app.delete('/api/delete', (req, res) => {

  console.log("req body ", req.body)

  if(req.body.item){
    // If we are deleting an individual item
    connection.query(`SELECT listID FROM remindr.list_prefs WHERE listName="${req.body.listName}"`, 
      function(err,rows){
        //get list id so we can delete all the items
        var listID = rows.map(row => row.listID)
        console.log("listID: ", listID)
        if(err) throw err;
        connection.query(
          `DELETE FROM remindr.list_items WHERE item="${req.body.item}" AND listID="${listID}" LIMIT 1`,
           function(err,rows) {
            if(err) throw err;
            res.json(["success"])
            }
        )
      }
    )
  } else if(req.body.list){

    // If we are deleting a list, delete all items with the corresponding list ID AND make approaching, alertRange and listName NULL
    connection.query(`SELECT listID FROM remindr.list_prefs WHERE listName="${req.body.list}"`, 
      function(err,rows){
        //get list id so we can delete all the items
        var listID = rows.map(row => row.listID)
        console.log("listID: ", listID)
        if(err) throw err;
        connection.query(
        `DELETE FROM remindr.list_items WHERE listID = "${listID}";
         UPDATE remindr.list_prefs SET approaching = NULL WHERE listName = "${req.body.list}";
         UPDATE remindr.list_prefs SET listName = NULL WHERE listName = "${req.body.list}"`,
         function(err,rows) {
          if(err) throw err;
          console.log("success")
          res.json(["success"])
        })
      })
    
  }

})

app.listen(3000)


