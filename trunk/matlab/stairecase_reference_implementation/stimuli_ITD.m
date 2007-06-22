function []=stimuli_ITD(reponse)
%Fonction stimuli mais adaptée à la rib, elle utilise un algorithme de staircase
global no sound reponse_patient bonne_reponse delay handler ref_side
global CurrVar
global indice_faux                                      %Tableau contenant les indice des réponses fausses
global indice_juste                                     %Tableau contenant les indice des réponses justes
global juste                                            %Tableau comportant les CurrVar juste
global faux                                             %Tableau comportant les CurrVar fausse
global counter                                          %Base de temps du graphique
global init_intens_d                                    %Intensité gauche utiliusé pour la référence (canal droite)
global init_intens_g                                    %Intensité gauche utiliusé pour la référence (canal gauche)
global no_elec_g                                        %Numéro de l'électrode gauche séléctioné au début de l'expérience
global no_elec_d                                        %Numéro de l'électrode droite séléctioné au début de l'expérience

reponse_patient(no)=reponse;                        %Sauvgarde de toutes les réponses données par le sujet

%Calcule du délai par l'algorithme du staircase
convergence=stairecase(reponse,bonne_reponse(no));              %Le nouveau delai est stocker dans la variable globale CurrVar
if convergence==0
    no=no+1;
    delay(no)=CurrVar;                                  %Sauvgarde du prochain délai
    %On doit arrondir delay vers un nombre impair (la stimulation stéréo est entrelacé et le canal de référence 'commence' à 0)
    if mod(delay(no)+1,2)~=0                    %Si delay(no) n'est pas impair (-> soit pair, soit pas entier)
        delay(no)=(2*round((delay(no)-1)/2))+1
    end

%Choix gauche-droite aléatoire
    direction = rand (1,1);
    if ref_side==1                  %Référence à gauche,seul le coté droite peut changer        
        if direction <= 0.5
            DioBurst(['BB,',num2str(init_intens_d),',',num2str(init_intens_g),',',num2str(init_intens_d),',',num2str(init_intens_g),',200000,500000,25000,25000,',num2str(-delay(no)),',0,0,0,1000']);
            %Droite -> délais négatif à droite    
            bonne_reponse(no) = 1;       %1 pour droite
            'droite'
        else    
            DioBurst(['BB,',num2str(init_intens_d),',',num2str(init_intens_g),',',num2str(init_intens_d),',',num2str(init_intens_g),',200000,500000,25000,25000,',num2str(delay(no)),',0,0,0,1000']);
            %Gauche -> delay positif à droite
            bonne_reponse(no) = 2;       %2 pour gauche
            'gauche'
        end
    elseif ref_side==2              %Référence à droite, seul le coté gauche peut bouger
        if direction <= 0.5
            DioBurst(['BB,',num2str(init_intens_d),',',num2str(init_intens_g),',',num2str(init_intens_d),',',num2str(init_intens_g),',200000,500000,25000,25000,0,',num2str(delay(no)),',0,0,1000']);
            %Droite -> délais positif à gauche    
            bonne_reponse(no) = 1;       %1 pour droite
            'droite'
        else
            DioBurst(['BB,',num2str(init_intens_d),',',num2str(init_intens_g),',',num2str(init_intens_d),',',num2str(init_intens_g),',200000,500000,25000,25000,0,',num2str(-delay(no)),',0,0,1000']);
            %Gauche -> delay négatif à gauche
            bonne_reponse(no) = 2;       %2 pour gauche
            'gauche'
        end
    else 
        'Mode de référence non définit'
    end        
%Activation des boutons 'gauche' et 'droite'
    set(handler.pushbutton8,'Enable','on');
    set(handler.pushbutton9,'Enable','on');
    set(handler.rejouer_button,'Enable','on');
elseif convergence==1
    'fermer toutes les fenètre etc'
    sauvegarde(indice_juste,indice_faux,delay);
    DioBurst('','');
    DioBurst('close');
    close(menu);
    clear all
    menu;
elseif convergence==-1
    'Attention, ITD initiale non idenetifiée!'
    DioBurst('','');
    DioBurst('close');
    close(menu);
    clear all
    menu;
end
delay