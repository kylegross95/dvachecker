function data2 = dva_postprocess_data_fitted( data )
%UNTITLED1 Summary of this function goes here
%  Detailed explanation goes here

va = [0.1 0.13 0.17 0.2 0.25 0.33 0.4 0.5 0.67 0.8 1 1.25 1.67 2];

data2 = []; 

for i=1:1:14,
   idxs = find(data(:,1)==va(i));
   subset = data(idxs, :);
   n_correct = sum( subset(:,2) )
   n_incorrect = size(subset,1) - n_correct; 
   
   % n_correct = uint32(n_correct);
   % n_incorrect = uint32(n_incorrect);
   
   n_trial = size(subset,1); 
   if (n_correct==0)
       percent_correct = 0; 
   else 
       percent_correct = 100 * n_correct / n_trial;
   end;
   
   
   if (n_trial>0)
    data2 = [data2;[ va(i) percent_correct n_trial]];
   end;
end
