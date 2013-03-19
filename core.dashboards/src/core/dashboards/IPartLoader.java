package core.dashboards;

public interface IPartLoader
{
     void BeginLoading();

     void Cancel();

     void Complete();

     void ExceptionOccured(system.Exception ex);

     void RefreshData();

}