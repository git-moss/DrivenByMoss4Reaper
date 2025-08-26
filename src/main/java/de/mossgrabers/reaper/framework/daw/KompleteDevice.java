// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.daw.data.IParameterList;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.bank.IDrumPadBank;
import de.mossgrabers.framework.daw.data.bank.ILayerBank;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.observer.IValueObserver;
import de.mossgrabers.reaper.framework.daw.data.SpecificDeviceImpl;


/**
 * Komplete Kontrol device.
 *
 * @author Jürgen Moßgraber
 */
public class KompleteDevice implements ISpecificDevice
{
    private final ISpecificDevice instrumentDevice;


    /**
     * Constructor.
     *
     * @param instrumentDevice The device to encapsulate
     */
    public KompleteDevice (final ISpecificDevice instrumentDevice)
    {
        this.instrumentDevice = instrumentDevice;
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        final String name = this.getName ();
        return name.contains ("Komplete Kontrol") || name.contains ("Kontakt 7") || name.contains ("Kontakt 8") || name.contains ("Maschine 3");
    }


    /** {@inheritDoc} */
    @Override
    public String getID ()
    {
        final String name = this.getName ();
        if (name.contains ("Komplete Kontrol"))
            return this.instrumentDevice.getID ();
        else if ((name.contains ("Kontakt 7") || name.contains ("Kontakt 8")) && this.instrumentDevice instanceof final SpecificDeviceImpl deviceImpl)
            return deviceImpl.getUnpagedParameterName (2048);
        else if (name.contains ("Maschine 3") && this.instrumentDevice instanceof final SpecificDeviceImpl deviceImpl)
            return deviceImpl.getUnpagedParameterName (128);
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public void remove ()
    {
        this.instrumentDevice.remove ();
    }


    /** {@inheritDoc} */
    @Override
    public void duplicate ()
    {
        this.instrumentDevice.duplicate ();
    }


    /** {@inheritDoc} */
    @Override
    public int getIndex ()
    {
        return this.instrumentDevice.getIndex ();
    }


    /** {@inheritDoc} */
    @Override
    public int getPosition ()
    {
        return this.instrumentDevice.getPosition ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSelected ()
    {
        return this.instrumentDevice.isSelected ();
    }


    /** {@inheritDoc} */
    @Override
    public void setSelected (final boolean isSelected)
    {
        this.instrumentDevice.setSelected (isSelected);
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        this.instrumentDevice.select ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMultiSelect ()
    {
        this.instrumentDevice.toggleMultiSelect ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.instrumentDevice.getName ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName (final int limit)
    {
        return this.instrumentDevice.getName (limit);
    }


    /** {@inheritDoc} */
    @Override
    public void addNameObserver (final IValueObserver<String> observer)
    {
        this.instrumentDevice.addNameObserver (observer);
    }


    /** {@inheritDoc} */
    @Override
    public void setName (final String name)
    {
        this.instrumentDevice.setName (name);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        this.instrumentDevice.enableObservers (enable);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isEnabled ()
    {
        return this.instrumentDevice.isEnabled ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleEnabledState ()
    {
        this.instrumentDevice.toggleEnabledState ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPlugin ()
    {
        return this.instrumentDevice.isPlugin ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isExpanded ()
    {
        return this.instrumentDevice.isExpanded ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleExpanded ()
    {
        this.instrumentDevice.toggleExpanded ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isParameterPageSectionVisible ()
    {
        return this.instrumentDevice.isParameterPageSectionVisible ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleParameterPageSectionVisible ()
    {
        this.instrumentDevice.toggleParameterPageSectionVisible ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isWindowOpen ()
    {
        return this.instrumentDevice.isWindowOpen ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleWindowOpen ()
    {
        this.instrumentDevice.toggleWindowOpen ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isNested ()
    {
        return this.instrumentDevice.isNested ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasDrumPads ()
    {
        return this.instrumentDevice.hasDrumPads ();
    }


    /** {@inheritDoc} */
    @Override
    public void addHasDrumPadsObserver (final IValueObserver<Boolean> observer)
    {
        this.instrumentDevice.addHasDrumPadsObserver (observer);
    }


    /** {@inheritDoc} */
    @Override
    public void removeHasDrumPadsObserver (final IValueObserver<Boolean> observer)
    {
        this.instrumentDevice.removeHasDrumPadsObserver (observer);
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasLayers ()
    {
        return this.instrumentDevice.hasLayers ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasSlots ()
    {
        return this.instrumentDevice.hasSlots ();
    }


    /** {@inheritDoc} */
    @Override
    public IParameterBank getParameterBank ()
    {
        return this.instrumentDevice.getParameterBank ();
    }


    /** {@inheritDoc} */
    @Override
    public ILayerBank getLayerBank ()
    {
        return this.instrumentDevice.getLayerBank ();
    }


    /** {@inheritDoc} */
    @Override
    public IDrumPadBank getDrumPadBank ()
    {
        return this.instrumentDevice.getDrumPadBank ();
    }


    /** {@inheritDoc} */
    @Override
    public IParameterList getParameterList ()
    {
        throw new UnsupportedOperationException ();
    }


    /** {@inheritDoc} */
    @Override
    public String getPresetName ()
    {
        return "";
    }
}
