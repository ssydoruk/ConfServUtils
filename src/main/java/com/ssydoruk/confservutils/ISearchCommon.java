/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssydoruk.confservutils;

import java.util.Collection;

/**
 *
 * @author stepan_sydoruk
 */
public interface ISearchCommon {

    public String getSearchSummary();

    public void setChoices(Collection<String> choices);

    public Collection<String> getChoices();

}
