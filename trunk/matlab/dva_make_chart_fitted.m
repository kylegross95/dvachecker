function [ output_args ] = dva_make_chart_fitted( name )
%UNTITLED1 Summary of this function goes here
%  Detailed explanation goes here

close all; 

datadirectory = 'C:\Documents and Settings\J-Chris\dvachecker_data\'; 

data_l0 = load([datadirectory, name, '\', name, '_left_0_data.dat']);
data_r0 = load([datadirectory, name, '\', name, '_right_0_data.dat']);
data_l5 = load([datadirectory, name, '\', name, '_left_5_data.dat']);
data_r5 = load([datadirectory, name, '\', name, '_right_5_data.dat']);
data_l9 = load([datadirectory, name, '\', name, '_left_9_data.dat']);
data_r9 = load([datadirectory, name, '\', name, '_right_9_data.dat']);

%data_r0 = Milena_Sedlak_right_0_data;
data_r0c = dva_postprocess_data_fitted(data_r0);
[r0_EST_P r0_OBS_S r0_SIM_P r0_SIM_S r0_LDOT] = psignifit(data_r0c);

%data_l0 = Milena_Sedlak_left_0_data;
data_l0c = dva_postprocess_data_fitted(data_l0);
[l0_EST_P l0_OBS_S l0_SIM_P l0_SIM_S l0_LDOT] = psignifit(data_l0c);

%data_r5 = Milena_Sedlak_right_5_data;
data_r5c = dva_postprocess_data_fitted(data_r5);
[r5_EST_P r5_OBS_S r5_SIM_P r5_SIM_S r5_LDOT] = psignifit(data_r5c);

%data_l5 = Milena_Sedlak_left_5_data;
data_l5c = dva_postprocess_data_fitted(data_l5);
[l5_EST_P l5_OBS_S l5_SIM_P l5_SIM_S l5_LDOT] = psignifit(data_l5c);

%data_r9 = Milena_Sedlak_right_9_data;
data_r9c = dva_postprocess_data_fitted(data_r9);
[r9_EST_P r9_OBS_S r9_SIM_P r9_SIM_S r9_LDOT] = psignifit(data_r9c);

%data_l9 = Milena_Sedlak_left_9_data;
data_l9c = dva_postprocess_data_fitted(data_l9);
[l9_EST_P l9_OBS_S l9_SIM_P l9_SIM_S l9_LDOT] = psignifit(data_l9c);

xmin = 0;
xmax = 2;
ymin = -0.1;
ymax = 1.1;

pfshape = 'Gumbel';
pf_threshold = 0.75; 

figure(1); 

subplot(3,1,1);
hold on;
plot(data_l0(:,1), data_l0(:,2), '*');
plotpf(pfshape, l0_EST_P);
[hdl, x_l0, y_l0] = plotpf_patched(pfshape, l0_EST_P);
c_l0 = findnearest(y_l0, pf_threshold, -1);
vline(x_l0(c_l0), 'r:', num2str(x_l0(c_l0)));
axis([xmin xmax ymin ymax]);
title('Left / 0kmh');

subplot(3,1,2);
hold on;
plot(data_l5(:,1), data_l5(:,2), '*');
[hdl, x_l5, y_l5] = plotpf_patched(pfshape, l5_EST_P);
c_l5 = findnearest(y_l5, pf_threshold, -1);
vline(x_l5(c_l5), 'r:', num2str(x_l5(c_l5)));
axis([xmin xmax ymin ymax]);
title('Left / 5kmh');

subplot(3,1,3);
hold on;
plot(data_l9(:,1), data_l9(:,2), '*');
[hdl, x_l9, y_l9] = plotpf_patched(pfshape, l9_EST_P);
c_l9 = findnearest(y_l9, pf_threshold, -1);
vline(x_l9(c_l9), 'r:', num2str(x_l9(c_l9)));
axis([xmin xmax ymin ymax]);
title('Left / 9kmh');

figure(2); 

subplot(3,1,1);
hold on;
plot(data_r0(:,1), data_r0(:,2), '*');
[hdl, x_r0, y_r0] = plotpf_patched(pfshape, r0_EST_P);
c_r0 = findnearest(y_r0, pf_threshold, -1);
vline(x_r0(c_r0), 'r:', num2str(x_r0(c_r0)));
axis([xmin xmax ymin ymax]);
title('Right / 0kmh');

subplot(3,1,2);
hold on;
plot(data_r5(:,1), data_r5(:,2), '*');
[hdl, x_r5, y_r5] = plotpf_patched(pfshape, r5_EST_P);
c_r5 = findnearest(y_r5, pf_threshold, -1);
vline(x_r5(c_r5), 'r:', num2str(x_r5(c_r5)));
axis([xmin xmax ymin ymax]);
title('Right / 5kmh');

subplot(3,1,3);
hold on;
plot(data_r9(:,1), data_r9(:,2), '*');
[hdl, x_r9, y_r9] = plotpf_patched(pfshape, r9_EST_P);
c_r9 = findnearest(y_r9, pf_threshold, -1);
vline(x_r9(c_r9), 'r:', num2str(x_r9(c_r9)));
axis([xmin xmax ymin ymax]);
title('Right / 9kmh');

