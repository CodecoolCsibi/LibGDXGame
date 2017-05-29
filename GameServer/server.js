require('babel-register');
var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);
const {Player} = require('./model/player');
const {Team} = require('./model/team-enum');

var players = [];

server.listen(8080, function(){
    console.log("server is now running...");
    });

io.on('connection', function(socket){
    socket.emit('socketID',
        {
            id: socket.id,
        });
    socket.emit('players', players);
    players.push(new Player());
        console.log("Player connected!");

    socket.on('disconnect', function(socket){
        socket.broadcast.emit('playerDisconnected', {id: socket.id});
        players.forEach(function (item, index){

            if (player.id == socket.id){
                players.splice(index, 1)
            }
        });
        console.log("Player disconnected.")
    });
    var player = new Player(1, 1, 1, Team.RED)
});


