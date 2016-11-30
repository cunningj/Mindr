var express = require('express')
var app = express();
var mysql      = require('mysql');
var connection = mysql.createConnection({
  host     : 'localhost',
  user     : 'root',
  password : 'basset'
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


//this route we want this info when we open a list
//when list is opened request should send list id and querey based on that
//select where list Id is req.listID
app.get('/api/locations',(req, res) => {
  connection.query('SELECT locationName FROM remindr.list_prefs WHERE locationName IS NOT NULL', function(err, rows){
  if(err) throw err;
  res.json(rows.map(row => row.locationName))

  })
})

app.get('/api/listPrefs', (req, res) => {
  connection.query('SELECT listName FROM remindr.list_prefs WHERE listName IS NOT NULL',function(err,rows){
    if(err) throw err;
    res.json(rows.map(row => row.listName))
  })
})


app.post('/api/listItems', (req, res) => {
  console.log("hi")
  connection.query(
  `SELECT i.item FROM remindr.list_items as i, remindr.list_prefs as p where p.listName ="${req.body.listName}" and i.listID=p.listID`,
   function(err,rows) {
    if(err) throw err;
    res.json(rows.map(row => row.item))

  })
})

app.post('/api/addList', (req, res) => {
  console.log("adding new list")
  console.log(req.body)
  connection.query(
  `INSERT INTO remindr.list_prefs (listName, approaching, alertRange, locationName) VALUES ("${req.body.listName}","${req.body.approaching}","${req.body.alertRange}","${req.body.locationName}")`,
   function(err,rows) {
    if(err) throw err;
    res.json(["success"])

  })
})

//insert into list_prefs
//we get a new id for the list
//select for the listID for the new list
//insert new listID with the new list items
//same query but for locations table


app.listen(3000)
