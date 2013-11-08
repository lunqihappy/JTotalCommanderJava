package layouts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.SwingUtilities;



public class Context {

	

    private final ArrayList<ContextChangeListener> listeners
        = new ArrayList<ContextChangeListener>();

	private Locale locale;
	private ResourceBundle bundle;
	private String resourceName;
	
	
	Context(String name){ resourceName=name;}

	/**
	 * @return Returns currently active resource bundle.
	 */
	public ResourceBundle getBundle() {
		return bundle;
	}

    /**
     * @return Returns currently active locale.
     */
	public Locale getLocale() {
		return locale;
	}

    /**
     * Sets new locale to be used by the GUI. This
     * method triggers an event propagated to all listeners.
     */
	public void setLocale(Locale locale) {
		if (locale.equals(this.locale)) {
			// same locale, no change.
			return;
		}

		// get resource bundle for this locale.
		ResourceBundle bundle = ResourceBundle.getBundle(resourceName, locale, this.getClass().getClassLoader());
		if (bundle == null) {
			throw new IllegalArgumentException("No resource bundle for: "
						+ locale.getLanguage());
		}
		this.locale = locale;
		this.bundle = bundle;

		fireContextChangedEvent();
	}

    /**
     * Adds a new {@link ContextChangeListener} to the list of
     * objects listening on the changes of this context.
     */
    public synchronized void addContextChangeListener(ContextChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Fires an event to all listeners. The event may be delayed
     * but will be delivered eventually. The order of calls to this
     * method is always preserved. 
     */
	private void fireContextChangedEvent() {
        // Event dispatcher code wrapped in a runnable.
        final Runnable dispatcher = new Runnable() {
            public void run() {
                synchronized (Context.this) {
                    for (Iterator<ContextChangeListener> i = listeners.iterator();i.hasNext();) {
                        i.next().contextChanged();
                    }
                }
            }
        };

        // The contract in ContextChangeListener states that
        // <code>contextChanged</code> method must be invoked
        // from the AWT thread. If we're the AWT thread, execute
        // immediately. Otherwise just enqueue the event dispatcher.
	    if (SwingUtilities.isEventDispatchThread()) {
            dispatcher.run();
        } else {
            SwingUtilities.invokeLater(dispatcher);
        }
	}
}