package server;

/**
 * Created by Mikaël on 2016-10-22.
 */

//Codes pour communication entre clients et serveur.
public enum ControlCode {

    //Codes envoyés par le client.
    APLUS, AMINUS, BPLUS, BMINUS, CPLUS, CMINUS, DPLUS, DMINUS,

    //Codes utilisés pour le fonctionnement interne du serveur.
    BLANK, A, B, C, D, NEWQUESTION, UPDATE, RESET,

    //Codes pour contrôler le nombre d'utilisateurs enregistrés.
    REG, UNREG,

}
