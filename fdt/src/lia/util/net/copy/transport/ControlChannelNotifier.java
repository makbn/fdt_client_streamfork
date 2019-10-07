/*
 * $Id$
 */
package lia.util.net.copy.transport;

import lia.util.net.common.FDTCloseable;

/**
 * Basic interface for all the classes interested in receiving the control
 * messages between the FDT peers
 *
 * @author ramiro
 */
public interface ControlChannelNotifier extends FDTCloseable {

    public void notifyCtrlMsg(ControlChannel controlChannel, Object ctrlMessage) throws FDTProcolException;

    public void notifyCtrlSessionDown(ControlChannel controlChannel, Throwable cause);

}
