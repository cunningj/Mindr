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
  console.log("adding new list")
  console.log(req.body)
  var re = /item*/;
  var itemsArr = [];

  // this function makes itemsArr an array of arrays with a single item in each (that is syntax required by SQL)
  for(key in req.body){
    if (key.match(re) && req.body[key]){
      var newArr = [];
      newArr.push(req.body[key]);
      itemsArr.push(newArr);
    }
  }

  // allows us to add lines to our query based on the number of items we have added from frontend
  var sql =  "";
  for (var i = 0; i<itemsArr.length; i++) {
    sql = sql + "INSERT INTO remindr.list_items (listID, item) VALUES (LAST_INSERT_ID(), '" + itemsArr[i] + "'); \n"
  }

  if (connection.query(`SELECT locationName FROM remindr.list_prefs WHERE locationName="${req.body.locationName}"`)){
    connection.query(`UPDATE remindr.list_prefs SET listName = "${req.body.listName}", approaching = "${req.body.approaching}", alertRange ="${req.body.alertRange}" WHERE locationName="${req.body.locationName}"`,

    function(err,rows) {
    if(err) throw err;
    res.json(["success"])

    })

  } else { connection.query(
    `INSERT INTO remindr.list_prefs (listName, approaching, alertRange, locationName) VALUES ("${req.body.listName}","${req.body.approaching}","${req.body.alertRange}","${req.body.locationName}");
    SELECT LAST_INSERT_ID();
    ${sql}`,

     function(err,rows) {
      if(err) throw err;
      res.json(["success"])

    })
  }})

app.post('/api/addLocation', (req, res) => {
  connection.query(
  `INSERT INTO remindr.list_prefs (locationName, latitude, longitude) VALUES ("${req.body.locationName}","${req.body.latitude}","${req.body.longitude}")`,
   function(err,rows) {
    if(err) throw err;
    res.json(["success"])
  })
})

app.listen(3000)