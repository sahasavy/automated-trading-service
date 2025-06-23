package invest.automate.trade.signal;

/**
 * A functional interface responsible for dispatching trading signals.
 */
public interface SignalDispatcher {
    void dispatch(Signal signal);

    /**
     * Basic lambda wrapper for signal consumers.
     */
    interface SignalConsumer extends SignalDispatcher {
        @Override
        default void dispatch(Signal signal) {
            accept(signal);
        }

        void accept(Signal signal);
    }
}
