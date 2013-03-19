package core.dashboards;

public interface ILoadingStatusSink
{
     void ChangeProgress(Integer currentValue, Integer maxValue);

}