var address = "ws://" + window.location.host + "/ws";
var changeTexton = function (numTexton, numLiens) {
    currentTexton = numTexton;
    disableControls();
    displayInstructions("Changement de module");
    setTimeout(function (param) {
        console.log("settimeout a marché");
        enableControls(param);
    }, 2000, numLiens);
};
var connecterror = "<span class='glyphicon glyphicon-exclamation-sign' aria-hidden='true'></span>Erreur de connexion. Rafraîchissez la page.";
var connectsuccess = "<span class='glyphicon glyphicon-exclamation-sign' aria-hidden='true'></span>Vous êtes connecté.";
var currentTexton = null;
var connectstatus;
var id = function (id) {
    return document.getElementById(id);
};
var instructionsarea;
var keepAlivePid = 0;
var vote = "NULL";
var votedisplay;
var ws;

var votes = ["a", "b", "c", "d"];
var testlinktexts = {
    "a": "Ceci est le lien A, qui a un très long texte.",
    "b": "Voici ce qui est le lien B",
    "c": "Et ceci est le lien C",
    "d": "Finalement, voici le lien D."
};
var votestextarea;

//Il faut que les liens internet marchent pour tester sans compiler, mais que les liens webjar marchent dans la distribution.
window.onload = function () {
    console.log(address);

    id("abutton").onclick = function () {
        this.blur();
        checkAndSend("A");
    };
    id("bbutton").onclick = function () {
        this.blur();
        checkAndSend("B");
    };
    id("cbutton").onclick = function () {
        this.blur();
        checkAndSend("C");
    };
    id("dbutton").onclick = function () {
        this.blur();
        checkAndSend("D");
    };
    id("annulerbtn").onclick = function () {
        this.blur();
        checkAndSend("NULL");
    };
    connectstatus = id("connectstatus");
    instructionsarea = id("instructionsarea");
    votedisplay = id("votedisplay");
    votestextarea = id("votestextarea");

    disableControls();

    ws = new WebSocket(address);

    ws.onopen = function () {
        displayConnMess();
        startKeepAlive();
    };

    ws.onmessage = function (msg) {
        if (msg.data === "pong") {
            console.log("pong reçu");
            return;
        }
        console.log("message reçu du serveur: " + msg.data);
        var msgobj = JSON.parse(msg.data);
        if (msgobj.hasOwnProperty("vote")) {
            vote = msgobj.vote;
            setVoteDisplay(vote);
        }
        //updateVoteTexts(testlinktexts);
        if (msgobj.hasOwnProperty("texts")) {
            updateVoteTexts(msgobj.texts);
        }

        if (msgobj.hasOwnProperty("numLiens") && msgobj.numLiens !== undefined && msgobj.hasOwnProperty("textonCourant")) {
            if (currentTexton !== msgobj.textonCourant && msgobj.textonCourant !== 0) {
                debugger;
                changeTexton(msgobj.textonCourant, msgobj.numLiens);
            }
            if (msgobj.textonCourant === 0) {
                disableControls();
                updateVoteTexts();
                displayInstructions("Vote désactivé");
            }
        }
    };

    ws.onclose = function () {
        disableControls();
        displayNotConnMess();
        displayInstructions("");
        stopKeepAlive();
    };
    ws.onerror = function () {
        disableControls();
        displayNotConnMess();
        displayInstructions("Erreur de connexion. Essayez de rafraîchir la page.");
        stopKeepAlive();
    };
};

function checkAndSend(str) {
    //No check implemented.
    ws.send(str);
}

window.onbeforeunload = function () {
    ws.close();
    return null;
};

function disableControls() {
    console.log("disabling controls");
    id("abutton").disabled = true;
    id("bbutton").disabled = true;
    id("cbutton").disabled = true;
    id("dbutton").disabled = true;
    id("annulerbtn").disabled = true;
}

function enableControls(numLiens) {
    console.log("enabling controls with " + numLiens);
    if (numLiens > 0)
        id("abutton").disabled = false;
    if (numLiens > 1)
        id("bbutton").disabled = false;
    if (numLiens > 2)
        id("cbutton").disabled = false;
    if (numLiens > 3)
        id("dbutton").disabled = false;
    id("annulerbtn").disabled = false;
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
    if (str === "") instructionsarea.innerHTML = "";
    instructionsarea.innerHTML = "<div class = 'well well-sm'><span class='glyphicon glyphicon-info-sign'></span>" + str + "</div>";
}

function startKeepAlive() {
    keepAlivePid = setInterval(function () {
        ws.send("ping");
    }, 3000);
}

function stopKeepAlive() {
    clearInterval(keepAlivePid);
}

function setVoteDisplay(vote) {
    var votedisplay = document.getElementById("votedisplay");
    var voteToDisplay;
    if (vote === "NULL")
        voteToDisplay = "Aucun";
    else
        voteToDisplay = vote;
    votedisplay.innerHTML = "Votre vote : " + voteToDisplay;
}


function updateVoteTexts(texts) {
    votes.forEach(function (vote, index) {
        id(vote + "btntext").innerHTML = "";
        if (texts[index])
            id(vote + "btntext").innerHTML += texts[index];
    });
}

//TODO Les anciens liens s'affichent encore à la conclusion, il faut les enlever.