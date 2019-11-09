/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.github.liachmodded.runorama.client;

public interface CloseableBinder extends AutoCloseable {

    /**
     * Bind an image for a face. Face varies from 0 to 6.
     *
     * @param face the face index
     */
    void bind(int face);

    /**
     * Disposes this binder.
     */
    @Override
    void close();
}
