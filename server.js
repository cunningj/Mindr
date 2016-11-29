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
  connection.query('SELECT locationName FROM remindr.locations', function(err, rows){
  if(err) throw err;
  res.json(rows.map(row => row.locationName))

  })
})

app.get('/api/listPrefs', (req, res) => {
  connection.query('SELECT listName FROM remindr.list_prefs',function(err,rows){
    if(err) throw err;
    res.json(rows.map(row => row.listName))
  })
})


app.post('/api/listItems', (req, res) => {
console.log(req);
        connection.query(
        `SELECT i.item FROM remindr.list_items as i, remindr.list_prefs as p where p.listName ="${req.body.listName}" and i.listID=p.listID`, 
         function(err,rows) {
          if(err) throw err;
  
          const listItems = rows.map(row => {
          const items = []
          for (var key in row) {
            if (key === 'item') {
              items.push(row[key])
            }
          }
          return {
            items: items
          }
        })
          console.log("THIS IS LIST ITEMS " + listItems);
        res.end(JSON.stringify(listItems))

        })
})



app.listen(3000)
