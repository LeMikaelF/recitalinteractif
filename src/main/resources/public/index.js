var currentTexton = null;
var connectstatus;
var instructionsarea;
var votedisplay;
var connecterror = "<span class='glyphicon glyphicon-exclamation-sign' aria-hidden='true'></span>Erreur de connexion. Rafraîchissez la page.";
var connectsuccess = "<span class='glyphicon glyphicon-exclamation-sign' aria-hidden='true'></span>Vous êtes connecté.";
var ws;
//TODO changer ceci pour 1.2.3.4
var address = 'ws://192.168.2.34:80/ws';
var id = function (id) {
    return document.getElementById(id);
};
var changeTexton = function (numTexton, numLiens) {
    currentTexton = numTexton;
    disableControls();
    displayInstructions('Changement de texton');
    setTimeout(function () {
        enableControls(numLiens);
    }, 2000);
};

//Il faut que les liens internet marchent pour tester sans compiler, mais que les liens webjar marchent dans la distribution.

window.onload = function () {
    id('abutton').onclick = function () {
        this.blur();
        checkAndSend('A');
    };
    id('bbutton').onclick = function () {
        this.blur();
        checkAndSend('B');
    };
    id('cbutton').onclick = function () {
        this.blur();
        checkAndSend('C');
    };
    id('dbutton').onclick = function () {
        this.blur();
        checkAndSend('D');
    };
    id('annulerbtn').onclick = function () {
        this.blur();
        checkAndSend('NULL')
    };
    connectstatus = id('connectstatus');
    instructionsarea = id('instructionsarea');
    votedisplay = id('votedisplay');

    disableControls();

    ws = new WebSocket(address);

    ws.onopen = function () {
        //TODO Est-ce qu'il faut activer les contrôles tout de suite, ou seulement quand un numéro de texton est reçu?
        enableControls();
        displayConnMess();
    };

    ws.onmessage = function (msg) {
        console.log('message reçu du serveur: ' + msg.data);
        var msgobj = JSON.parse(msg.data);
        enableControls(msgobj.numLiens);
        if (currentTexton !== msgobj.textonCourant && msgObj.textonCourant !== 0) {
            changeTexton(msgobj.textonCourant);
        }
        if(msgobj.textonCourant === 0){
            disableControls();
            displayInstructions('Vote désactivé');
        }
    };

    ws.onclose = function () {
        disableControls();
        displayNotConnMess();
        displayInstructions('');

        ws.onerror = function () {
            disableControls();
            displayNotConnMess();
            displayInstructions('Erreur de connexion. Essayez de rafraîchir la page.');
        }
    }
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
    id('abutton').disabled = true;
    id('bbutton').disabled = true;
    id('cbutton').disabled = true;
    id('dbutton').disabled = true;
    id('annulerbtn').disabled = true;
}

function enableControls(numLiens) {
    if (numLiens > 0 && numLiens < 5) {
        if (numLiens > 0)
            id('abutton').disabled = false;
        if (numLiens > 1)
            id('bbutton').disabled = false;
        if (numLiens > 2)
            id('cbutton').disabled = false;
        if (numLiens > 3)
            id('dbutton').disabled = false;
        id('annulerbtn').disabled = false;
    }
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

