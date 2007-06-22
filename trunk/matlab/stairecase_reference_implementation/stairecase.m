function convergence=stairecase(resp,stim_int)
%Fonction qui calcule le prochain stimuli selon un algo de staircase
%resp:réponse du sujet
%stim_int: réponse juste
global stimulus	                                        %=1 if heard it, =-1 if didn't hear it, =0 if change stimulus, no lastresp
global rundir                                           %La direction montant = 1,descendant =-1
global step                                             %Le pas courant
global CurrVar                                          %La variable dur staircase
global run                                              %Le nombre de segment montant et descendant
global peak                                             %Tableau contenant les pics
global valley                                           %Tableau contenant les vallée
global limite_up                                        %Limite supérieur pour la variable du staircase (700 us par exemple)
global limit_down                                       %Limite inférieur pour la variable du staircase (0 us par exemple)
global minstep;                                         %Pas minimum accordé
global no                                               %numéro du stimuli courant
global indice_faux                                      %Tableau contenant les indice des réponses fausses
global indice_juste                                     %Tableau contenant les indice des réponses justes
global juste                                            %Tableau comportant les CurrVar juste
global faux                                             %Tableau comportant les CurrVar fausse
%global counter                                          %Base de temps du graphique

%Variable locale

convergence=0;                                          %Flag: 1 -> convergence, 0 -> non, -1 -> divergence
nbofrun=12;                                              %Nb de segment totale désiré (condition de sortie)

%Algorithme de staircase

switch(rundir)                                          %Run dir vaut 1 ou -1 (monte ou descend)
   case 1 					                            %On monte
   if stim_int~=resp;	                                %Réponse fausse
        %counter=counter+1;                             %Base de temps du graphique
        faux=[faux,CurrVar];                            %Sauvgarde des CirrVarr faux
        indice_faux=[indice_faux,no];                   %Indice de cette faute relativement à la base de temps
        CurrVar=CurrVar+step;                           %On augmente la variable
        if CurrVar>limite_up                           %Mais elle ne dois pas dépasser cette limite
            'convergence=-1'
            convergence=-1;
        end
        stimulus=0; 		                            %Flag indiquant que le stimuli a été changé
    elseif stimulus==1; 	                            %La réponse précédante et la réponse actuelle sont justes
        %counter=counter+1;                              %Base de temps du graphique
        juste=[juste,CurrVar];                          %Sauvgarde des CirrVarr juste
        indice_juste=[indice_juste,no];                 %Indice de cette réponse juste
        peak=[peak,CurrVar];                            %On prend note du fait que l'on a un pic
        rundir=-1;                                      %Car la direction a changé
        run=run+1;                                      %On augmente le nombre de segment
        switch (run)                                    %Changement du step
            case 3                                      %Au troisième segment
            if step/2<=minstep;                          %On diminue de moitier mais on ne doit pas etre au dessous du seuil minimum
                step=minstep;
            else step=step/2;
            end
            case 5                                      %Au 5ème segment descendant
            if step/2<=minstep;                          %On diminue de moitier mais on ne doit pas etre au dessous du seuil minimum
                step=minstep;
            else step=step/2;
            end
        end
        CurrVar=CurrVar-step;                           %On diminue la varialble
        if CurrVar<limit_down                                    
            CurrVar=limit_down;                         %La variable ne doit pas se situé en dessous de cette limite
        end
        stimulus=0;                                     %Flag indiquant que le stimuli a été changé
   else stimulus=1;  	                                %La réponse précédante a été fausse mais la courante juste
     	                                                %Flag indiquant que le stimuli n'a pas été changé
        juste=[juste,CurrVar];                          %Sauvgarde des CirrVarr juste
        indice_juste=[indice_juste,no];                 %Indice de cette réponse juste
   end
     
   case -1 						                        %On descend
        if stim_int~=resp; 	                            %La réponse est fausse
            %counter=counter+1;                          %Base de temps du graphique
            faux=[faux,CurrVar];                        %Sauvgarde des CirrVarr faux
            indice_faux=[indice_faux,no];               %Indice de cette faute relativement à la base de temps
            valley=[valley,CurrVar];                    %Comme on monte, on prend note de la vallée            
            CurrVar=CurrVar+step;                       %On augmente la variable
            if CurrVar>limite_up                       %Mais elle ne dois pas dépasser cette limite
                'convergence=-1'
                convergence=-1;                         %Divergence
            end
            rundir=1;                                   %On change de direction
            run=run+1;                                  %On augmente le nombre de segment
            if run>=nbofrun;                          %Si le nombre de segment maximum a été atteint (condition de sortie)
                'Convergence=1'
	            convergence=1;
	 
                        
            %minima=valley(4:6);                         %Calcule de la valeur moyenn et déviation standard sur les                        
            %maxima=peak(4:6);                           %trois dernier pics/vallée
            %mean=sum(minima,maxima)/6;
            %stdev=sqrt(1/5*(sum((minima-mean).^2)+sum((maxima-mean).^2)));
            
            end
            stimulus=0;                                 %On doit changé de direction (pas besoin de restimuler puisque l'on descend)
        elseif stimulus==1;	                            %Réponse précédante et actuelle juste
            %counter=counter+1;                         %Base de temps du graphique
            juste=[juste,CurrVar];                      %Sauvgarde des CirrVarr juste
            indice_juste=[indice_juste,no];             %Indice de cette réponse juste
            CurrVar=CurrVar-step;                       %On diminue mais pas au delà de la valeur limite
            if CurrVar<limit_down
                CurrVar=limit_down;
            end 			                            
            stimulus=0; 			                        %Flag indiquant que le stimuli a été changé
            
        else 
            stimulus=1; 		                        %Réponse précédante fausse mais actuel juste
                                                            %On envoie le m^eme stimui pour confirmer
            juste=[juste,CurrVar];                          %Sauvgarde des CirrVarr juste
            indice_juste=[indice_juste,no];                 %Indice de cette réponse juste
                                             
        end
end                                                     %Fin du switch
run
      

