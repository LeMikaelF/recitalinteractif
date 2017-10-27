package events;

/**
 * Created by Mikaël on 2017-10-01.
 */
public enum ControlEvent {

    //Pour débuter le récital (aller au texton 1), peut être invoqué après la conclusion (à implémenter)
    COMMENCER,

    //Pour aller à la conclusion du récital.
    CONCLUSION,

    //Pour passer au texton suivant pendant la présentation.
    TERMINE,

    //Pour quitter le programme.
    SHUTDOWN,

    //Pour activer et désactiver la simuation physique du graphe pendant la conclusion.
    PHYSON, PHYSOFF,

    //Pour envoyer des frappes de touches sur le EventBus.
    KEYTYPED,

    //Ceci est utilisé pendant la conclusion pour passer au noeud suivant sur le graphe.
    SUIVANT
}
