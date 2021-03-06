/*
 * Copyright 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an
 * "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.powertac.common

import org.codehaus.groovy.grails.commons.ApplicationHolder
//import org.joda.time.DateTime
import org.joda.time.Instant

 /**
 * A timeslot instance describes a duration in time (slot). Time slots are used (i) to
 * correlate tradeable products (energy futures) and trades in the market with a future time
 * interval where settlement (i.e. delivery / consumption) has to take place, (ii) to
 * correlate meter readings with a duration in time, (iii) to  allow tariffs to define
 * different consumption / production prices for different times of a day
 *
 * @author Carsten Block
 * @version 1.0 - Feb,6,2011
 */
class Timeslot implements Serializable 
{
  static TimeService getTimeService()
  {
    ApplicationHolder.application.mainContext.timeService
  }

  String id = IdGenerator.createId()

  /**
   * used to find succeeding / preceding timeslot instances
   * @see {@code Timeslot.next()} {@code Timeslot.previous()}
   */
  Integer serialNumber

  /** flag that determines enabled state of the slot. E.g. in the market only orders for enabled timeslots are accepted. */
  Boolean enabled = false

  /** indicates that this timeslot is the present / now() timeslot in the competition */
  Boolean current = false

  /** start date and time of the timeslot */
  Instant startInstant

  /** end date and time of the timeslot */
  Instant endInstant
  
  static auditable = true
  
  static transients = ['timeService']

  static hasMany = [tariffTx: TariffTransaction, orderbooks: Orderbook, transactionLogs: TransactionLog, shouts: Shout]

  static constraints = {
    id (nullable: false, blank: false, unique: true)
    serialNumber(nullable: false)
    //competition(nullable: false)
    enabled(nullable: false)
    current(nullable: false)
    startInstant(nullable: false)
    endInstant(nullable: false)
  }

  static mapping = {
    id (generator: 'assigned')
    current(index:'ts_current_idx')
  }

  public String toString() {
    return "$startInstant - $endInstant";
  }

  /**
   * Note that this scheme for finding the current timeslot relies on the
   * time granularity reported by the timeService being the same as the length
   * of a timeslot.
   */
  public static Timeslot currentTimeslot() 
  {
    return Timeslot.findByStartInstant(timeService.currentTime)
  }

  public Timeslot next() {
    return Timeslot.findBySerialNumber(this.serialNumber + 1)
  }

  public Timeslot previous() {
    return Timeslot.findBySerialNumber(this.serialNumber - 1)
  }

}
