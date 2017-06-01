require('babel-register');
var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);
//var {Player} = require('./model/player');
//var {Team} = require('./model/team-enum');

var players = [];
var red = 0;
var blue = 0;

function Player (id, x, y) {
    this.id = id;
    this.x = x;
    this.y = y;
    this.team = null;

}

class Team {
    constructor(name) {
        this.name = name;
    }
    toString() {
        return `Team.${this.name}`;
    }
}
Team.RED = new Team('RED');
Team.BLUE = new Team('BLUE');


server.listen(8080, function(){
    console.log("server is now running...");
    });

io.on('connection', function(socket){

    var newPlayer = new Player(socket.id, 300, 400);
    if (red > blue) {
        newPlayer.team = Team.BLUE.name;
        blue++;
    } else {
        newPlayer.team = Team.RED.name;
        red++;
    }
    console.log("Player is connecting to " + newPlayer.team.name + " team...");
    players.push(newPlayer);
    socket.emit('socketID',
        {
            id: socket.id,
            team: newPlayer.team
        }
    );
    socket.emit('players', players);
    socket.broadcast.emit('newPlayer',
        {
            id: socket.id,
            x: newPlayer.x,
            y: newPlayer.y,
            team: newPlayer.team
        }
    );

    console.log("Player connected.");

    socket.on('disconnect', function(){
        socket.broadcast.emit("playerDisconnected", {id: socket.id});
        players.forEach(function (player, index){
            if (player.id == socket.id){
                players.splice(index, 1);
                console.log("Player deleted.")
            }
        });
        console.log("Player disconnected.")
    });

    socket.on("playerMoved", function(data){
        data.id = socket.id;
        socket.broadcast.emit("playerMoved", data);
        players.forEach(function (player){
            if (player.id == socket.id){
                players.x = data.x;
                players.y = data.y;

            }
            console.log("Players: " + players)
        });
    });

});


