// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.utils;

/**
 * Support methods for generating notes.
 *
 * @author Jürgen Moßgraber
 */
public class NoteGenerator
{
    /**
     * Generates a Euclidean rhythm using the Bjorklund algorithm. Effective pulses = floor(Pulse *
     * Density). This preserves Euclidean spacing while thinning the pattern.
     *
     * @param pulse number of onsets (k) in the range [0, length]
     * @param length total number of steps (n), must be > 0
     * @param rotation clockwise rotation (positive = right shift)
     * @param density normalized value in the range [0.0, 1.0] that scales how many of the generated
     *            pulses remain active.
     * @return Every 'true' represents a pulse
     */
    public static boolean [] generateEuclideanPattern (final int pulse, final int length, final int rotation, final double density)
    {
        if (length <= 0)
            throw new IllegalArgumentException ("Length must be > 0");
        if (pulse < 0 || pulse > length)
            throw new IllegalArgumentException ("Pulse must be in range [0, Length]");
        if (density < 0.0 || density > 1.0)
            throw new IllegalArgumentException ("Density must be in range [0.0, 1.0]");

        // Apply density scaling
        final int effectivePulses = (int) Math.floor (pulse * density);
        if (effectivePulses == 0)
            return new boolean [length];

        // Bjorklund algorithm
        final int [] pattern = new int [length];
        final int [] counts = new int [length];
        final int [] remainders = new int [length];

        int divisor = length - effectivePulses;
        remainders[0] = effectivePulses;
        int level = 0;

        while (true)
        {
            counts[level] = divisor / remainders[level];
            remainders[level + 1] = divisor % remainders[level];
            divisor = remainders[level];
            level++;
            if (remainders[level] <= 1)
                break;
        }
        counts[level] = divisor;

        // Build pattern recursively
        class Builder
        {
            int index = 0;


            void build (final int lvl)
            {
                if (lvl == -1)
                    pattern[this.index++] = 0;
                else if (lvl == -2)
                    pattern[this.index++] = 1;
                else
                {
                    for (int i = 0; i < counts[lvl]; i++)
                        this.build (lvl - 1);
                    if (remainders[lvl] != 0)
                        this.build (lvl - 2);
                }
            }
        }

        new Builder ().build (level);

        // Convert to boolean array
        boolean [] result = new boolean [length];
        for (int i = 0; i < length; i++)
            result[i] = pattern[i] == 1;

        // Apply rotation (right shift)
        final boolean [] rotated = new boolean [length];
        final int r = ((rotation + 1) % length + length) % length;
        for (int i = 0; i < length; i++)
            rotated[(i + r) % length] = result[i];
        return rotated;
    }
}
