package indi.kwanho.pm.common;

public interface PowerState {
    void attach(PowerObserver observer);
    void detach(PowerObserver observer);
    void notifyObservers();
}
