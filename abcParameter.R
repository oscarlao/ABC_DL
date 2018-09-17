rm(list=ls());
if("abc" %in% rownames(installed.packages()) == FALSE)
{
 stop("abc package is required"); 
}
library("abc");
abc_folder <- “path_to_abc_dl”; # CHANGE TO THE ABC_DL PATH
model.to.do <- "Model_B"; # CHANGE TO THE NAME OF THE MODEL
number.of.parameters.of.model <- 12; # CHANGE TO THE NUMBER OF PARAMETERS OF YOUR MODEL
abc.oscar <- function(target, param, sumstat, tol, method){
  abc.result <- matrix(nrow=tol*nrow(sumstat),ncol=ncol(param));
  colnames(abc.result) <- colnames(param);
  for(col in 1:ncol(param))  {
    target.s <- target[col];
    sumstat.s <- sumstat[,col];
    run.abc <- abc(target.s,param[,col],sumstat.s,tol = tol,method=method);
    if(method=="rejection")    {
      if(col==1)
      {
        abc.result <- matrix(nrow=nrow(run.abc$unadj.values),ncol=ncol(param));
      }
      abc.result[,col] <- run.abc$unadj.values;
    }
    else {
      if(col==1)
      {
        abc.result <- matrix(nrow=nrow(run.abc$adj.values),ncol=ncol(param));
      }      
      abc.result[,col] <- run.abc$adj.values;
    }
  }
  return(abc.result);
}
  data.t <- read.table(file=paste(abc_folder,model.to.do,"_replication_parameter_for_abc.txt",sep=""),header=T);
  data.tt <- data.t[-1,];  
  number.of.replicates.by.parameter <- ncol(data.t)/number.of.parameters.of.model - 1;
  param <- 9; # Parameter that we want to estimate.
  print(names(data.tt)[((number.of.replicates.by.parameter+1)*(param-1)+1)]);
  sim.param <- data.tt[,((number.of.replicates.by.parameter+1)*(param-1)+1)];
  start <- ((number.of.replicates.by.parameter+1)*(param-1)+2);
  end <- ((number.of.replicates.by.parameter+1)*(param-1)+2+(number.of.replicates.by.parameter-1));
  if(start!=end)  {
    ss.pred <- rowMeans(data.tt[,start:end]);
    observed <- mean(data.t[1,start:end]);
  } else {
    ss.pred <- data.tt[,start];
    observed <- data.t[1,start];
  }
  result.matrix <- abc.oscar(observed,as.matrix(sim.param),as.matrix(ss.pred),1000/nrow(data.t),"loclinear");
