var currentVote = null;
var currentTexton = null;
var connectstatus;
var instructionsarea;
var votedisplay;
var connecterror = "<span class='glyphicon glyphicon-exclamation-sign' aria-hidden='true'></span>Erreur de connexion. Rafraîchissez la page.";
var connectsuccess = "<span class='glyphicon glyphicon-exclamation-sign' aria-hidden='true'></span>Vous êtes connecté."
var ws = null;
var firstVoteSent = false;
window.onload = function () {

    document.getElementById("abutton").onclick = function () {
        this.blur();
        checkAndSend("A");
    };
    document.getElementById("bbutton").onclick = function () {
        this.blur();
        checkAndSend("B");
    };
    document.getElementById("cbutton").onclick = function () {
        this.blur();
        checkAndSend("C");
    };
    document.getElementById("dbutton").onclick = function () {
        this.blur();
        checkAndSend("D");
    };
    document.getElementById("annulerbtn").onclick = function () {
        this.blur();
        cancelVote();
    };

    connectstatus = document.getElementById("connectstatus");
    instructionsarea = document.getElementById("instructionsarea");
    votedisplay = document.getElementById("votedisplay");

    disableControls();
    var ip = prompt("Entrer l'adresse IP donnée.", "192.168.2.13");
    if (ip == null) {
        displayNotConnMess();
    }
    else {
        var fullIp = "ws://" + ip.toString() + ":5555/";
        ws = new WebSocket(fullIp);
    }

    ws.onopen = function () {
        enableControls();
        displayConnMess();
        displayInstructions("En attente du serveur…");
    };

    ws.onmessage = function (event) {
        console.log("Message reçu du serveur : " + event.data);
        //ws.send("BLANK");
        console.log(event.data.includes(";"));
        if (event.data.includes(";")) {
            var msg = event.data.split([";"]);
            console.log("CurrentTexton" + currentTexton);
            if (msg[1] !== currentTexton || currentTexton == null) {
                currentTexton = msg[1];
                currentVote = null;
                disableControls();
                displayInstructions("Changement de texton. Veuillez patienter…");
                setTimeout(function () {
                    console.log("Nombre de liens : " + msg[0]);
                    enableControls(msg[0]);
                }, 3000);
            }
        }

    };

    ws.onclose = function () {
        console.log("Connexion terminée." + "<br>");
        disableControls();
        displayNotConnMess();
        displayInstructions("");
    };

    ws.onerror = function (err) {
        displayInstructions("Erreur de connexion : " + err + "<br>Veuillez rapporter l'erreur à la fin du récital.");
        displayNotConnMess();
        console.log("Erreur de connexion : " + err);
    };
};


window.onbeforeunload = function () {
    ws.close();
    return null;
};

function disableControls() {
    document.getElementById("abutton").disabled = true;
    document.getElementById("bbutton").disabled = true;
    document.getElementById("cbutton").disabled = true;
    document.getElementById("dbutton").disabled = true;
    document.getElementById("annulerbtn").disabled = true;
    displayInstructions("Panneau d'interactions désactivé.");
}

function enableControls(num) {
    if (num > 0 && num < 5) {
        if (num > 0)
            document.getElementById("abutton").disabled = false;
        if (num > 1)
            document.getElementById("bbutton").disabled = false;
        if (num > 2)
            document.getElementById("cbutton").disabled = false;
        if (num > 3)
            document.getElementById("dbutton").disabled = false;
        document.getElementById("annulerbtn").disabled = false;
        displayInstructions("Panneau d'interactions activé.");
    }
}

function sendMessage(val) {
    votedisplay.innerHTML = "Votre vote : " + val;
    if (val !== null && firstVoteSent) {
        switch (currentVote) {
            case "A":
                ws.send("AMINUS");
                break;
            case "B":
                ws.send("BMINUS");
                break;
            case "C":
                ws.send("CMINUS");
                break;
            case "D":
                ws.send("DMINUS");
                break;
            default:
                break;
        }
    }
    firstVoteSent = true;
    ws.send(val + "PLUS");
    console.log("Message " + val + " sent.")
    currentVote = val;
}

function checkAndSend(str) {
    //Send message, but first check if vote is already sent.
    if (currentVote == str){
        console.log("currentVote is equivalent to pressed button.")
        return;
    }
    sendMessage(str);
}

function cancelVote() {
    if (currentVote == null) {
        console.log("currentVote is null.");
        return;
    }
    ws.send(currentVote + "MINUS");
    currentVote = null;
    votedisplay.innerHTML = "Votre vote est annulé.";
}

function displayConnMess() {
    connectstatus.className = "alert alert-success";
    connectstatus.innerHTML = connectsuccess;
}

function displayNotConnMess() {
    connectstatus.className = "alert alert-danger";
    connectstatus.innerHTML = connecterror;
}

function displayInstructions(str) {
    if (str == "") instructionsarea.innerHTML = "";
    instructionsarea.innerHTML = "<div class = 'well well-sm'><span class='glyphicon glyphicon-info-sign'></span>" + str + "</div>";
}