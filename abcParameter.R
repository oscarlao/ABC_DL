##########################################

#  PARAMETERS                            #

##########################################


rm(list=ls());
library("abc");

abc.oscar <- function(target, param, sumstat, tol, method)
{
  abc.result <- matrix(nrow=tol*nrow(sumstat),ncol=ncol(param));
  colnames(abc.result) <- colnames(param);
  for(col in 1:ncol(param))
  {
    print(col);
    # target weighted distance is 0 with himself
    #transformation <- c("logit");
    #boundaries <- matrix(nrow= 1,ncol=2);
    #boundaries[1,1] <- min(param[,col]);
    #boundaries[1,2] <- max(param[,col]);
    target.s <- target[col];
    sumstat.s <- sumstat[,col];
    #    run.abc <- abc(target.s,param[,col],sumstat.s,tol = tol,method=method, transf=transformation, logit.bounds = boundaries);
    run.abc <- abc(target.s,param[,col],sumstat.s,tol = tol,method=method); #, transf=transformation);
    if(method=="rejection")
    {
      if(col==1)
      {
        abc.result <- matrix(nrow=nrow(run.abc$unadj.values),ncol=ncol(param));
      }
      abc.result[,col] <- run.abc$unadj.values;
    }
    else
    {
      if(col==1)
      {
        abc.result <- matrix(nrow=nrow(run.abc$adj.values),ncol=ncol(param));
      }      
      abc.result[,col] <- run.abc$adj.values;
    }
  }
  return(abc.result);
}

for(kp in 1:length(models))
{
  model.to.do <- models[kp];
  data.t <- read.table(file=paste("C:\\Users\\olao\\Documents\\Ongoing_projects\\Jaume_Indian_Archaic\\abcSimulations\\unknown\\",model.to.do,"\\",model.to.do,"_replication_parameter_for_abc.txt",sep=""),header=T);
  observed.t <- read.table(file = paste("C:\\Users\\olao\\Documents\\Ongoing_projects\\Jaume_Indian_Archaic\\abcSimulations\\unknown\\",model.to.do,"\\",model.to.do,"_observed_for_abc.txt",sep=""), header=T);
  
  data.tt <- data.t;
  
  result.matrix <- matrix(nrow=1000,ncol=ncol(observed.t));
  
  colnames(result.matrix) <- colnames(observed.t);
  
  for(param in 1:ncol(observed.t))
  {
    print(names(observed.t)[(param+1)]);
    result.matrix[,param] <- abc.oscar(observed.t[1,param],as.matrix(data.t[,(2*(param-1)+1)]),as.matrix(data.t[,(2*(param-1)+2)]),1000/nrow(data.t),"loclinear");
  }
  colnames(result.matrix) <- names(observed.t);
  result.copy <- result.matrix;
  
  exo <- colnames(result.copy);
  
  for(col in 1:length(exo))
  {
    is.composited <- strsplit(exo[col],"[.]")[[1]];
    if(length(is.composited)>1)
    {
      id <- which(exo==is.composited[1]);
      if(length(id)==0)
      {
        print(exo[col]);
      }
      else
      {
        print(colnames(result.copy)[col]);
        exo[col] <- is.composited[2];
        result.copy[,col] <- result.copy[,id]*result.copy[,col];
        data.tt[,2*(col-1)+1] = data.tt[,2*(col-1)+1] * data.tt[,2*(id-1)+1];
      }
    }
  }
  
  CI <- matrix(nrow=ncol(result.copy),ncol=2);
  
  for(c in 1:ncol(result.copy))
  {
    CI[c,] <- quantile(result.copy[,c],probs=c(0.025,0.975));
  }
  
  write.table(result.copy, file=paste("C:\\Users\\olao\\Documents\\Ongoing_projects\\Jaume_Indian_Archaic\\abcSimulations\\unknown\\",model.to.do,"\\",model.to.do,"_output_result.txt",sep=""), row.names = FALSE);
  
  result <- data.frame(colMeans(result.copy),CI);
  
  write.table(result,file=paste("C:\\Users\\olao\\Documents\\Ongoing_projects\\Jaume_Indian_Archaic\\abcSimulations\\unknown\\",model.to.do,"\\",model.to.do,"_output.txt",sep=""));
  
  pdf(file=paste("C:\\Users\\olao\\Documents\\Ongoing_projects\\Jaume_Indian_Archaic\\abcSimulations\\unknown\\",model.to.do,"\\",model.to.do,"_output.pdf",sep=""));
  
  for(t in seq(from=1,to=ncol(observed.t),by=4))
  {
    par(mfrow=c(1,1));
    par(mfrow=c(2,2));  
    for(i in t:min((t+3),ncol(observed.t)))
    {
      print(c(t,i));
      minimum <- min(c(result.copy[,i],data.tt[,(2*(i-1)+1)]));
      maximum <- max(c(result.copy[,i],data.tt[,(2*(i-1)+1)]));
      plot(density(result.copy[,i]),main="", xlim = c(minimum,maximum), xlab=exo[i]);
      lines(density(data.tt[,(2*(i-1)+1)]),col="red");
    }
  }
  dev.off();
  
  par(mfrow=c(1,1));
  
  write.table(file=paste("C:\\Users\\olao\\Documents\\Ongoing_projects\\Jaume_Indian_Archaic\\abcSimulations\\unknown\\",model.to.do,"\\",model.to.do,"_posterior.txt",sep=""),result);
}


###################
# correlations    #
###################

models <- c("ModelDavidAllGhostNeanderthal");
for(kp in 1:length(models))
{
  model.to.do <- models[kp];
  data.t <- read.table(file=paste("C:\\Users\\Oscar Lao\\Documents\\David_Chromosomes\\ABC\\output_parameters\\",model.to.do,"_replication_parameter_for_abc.txt",sep=""),header=T);
  observed.t <- read.table(file = paste("C:\\Users\\Oscar Lao\\Documents\\David_Chromosomes\\ABC\\output_parameters\\",model.to.do,"_observed_for_abc.txt",sep=""), header=T);
  
  
  pdf(file= paste("C:\\Users\\Oscar Lao\\Documents\\David_Chromosomes\\ABC\\output_parameters\\",model.to.do,"_correlations.pdf",sep=""));
  
  correlations <- rep(NA,ncol(observed.t));
  
  names.variables <- c("");
  
  c <- 1;
  
  for(t in seq(from=1,to=ncol(observed.t),by=4))
  {
    par(mfrow=c(1,1));
    par(mfrow=c(2,2));  
    for(i in t:min((t+3),ncol(observed.t)))
    {
      print(c(t,i));
      corre <- cor(data.t[,(2*(i-1)+1)],data.t[,(2*(i-1)+2)]);
      correlations[c] <- corre;
      names.variables <- c(names.variables,names(data.t)[(2*(i-1)+1)]);
      #plot(data.t[1:5000,(2*(i-1)+1)], data.t[1:5000,(2*(i-1)+2)], main=paste("Correlation =",corre, sep=" "), xlab = names(data.t)[(2*(i-1)+1)], ylab=names(data.t)[(2*(i-1)+2)]);
      #abline(lm(data.t[1:5000,(2*(i-1)+2)] ~ data.t[1:5000,(2*(i-1)+1)]),col="red");
      c = c+1;
    }
  }
  names(correlations) <- names.variables[-1];
  write.table(file=paste("C:\\Users\\Oscar Lao\\Documents\\David_Chromosomes\\ABC\\output_parameters\\",model.to.do,"_correlations.txt",sep=""),correlations);
  
  dev.off();
  
  par(mfrow=c(1,1));
}