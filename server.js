var express = require('express')
var app = express();
var mysql      = require('mysql');
var connection = mysql.createConnection({
  host     : 'localhost',
  user     : 'root',
  password : 'basset'
});


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
app.get('/api/getListItems',(req, res) => {
  connection.query('SELECT * FROM remindr.list_items', function(err, rows){
    if(err) throw err;
    console.log('NEW DATA received from Db:\n');
  const todoLists = rows.map(row => {
    const items = []
    for (var key in row){
//      if (Number(key)){
        if (key !== 'listID'){
        items.push(row[key])
      }
    }
    return {
      items : items,
      listID : row.listID
    }
  })
  console.log('todoLists', todoLists)
  res.end(JSON.stringify(todoLists));
  })
})

connection.query('SELECT * FROM remindr.list_items',function(err,rows){
  if(err) throw err;
  console.log('Data received from Db:\n');
  console.log(rows);
})

app.listen(3000)
