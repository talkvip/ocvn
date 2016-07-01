import React from "react";
import Component from "../pure-render-component";
import BiddingPeriod from "./bidding-period";
import Cancelled from "./cancelled";
import CancelledPercents from "./cancelled-percents";
import {toImmutable} from "nuclear-js";
import Comparison from "../comparison";
import translatable from "../translatable";

export default class Tender extends translatable(Component){
  render(){
    let {state, width, actions, translations} = this.props;
    let {compare, bidPeriod, cancelled, showPercentsCancelled} = state;
    return (
        <div className="col-sm-12 content">
          {compare ?
              <Comparison
                  translations={translations}
                  width={width}
                  state={bidPeriod}
                  Component={BiddingPeriod}
                  title={this.__("Bid period")}
              />
              :
              <BiddingPeriod
                  translations={translations}
                  title={this.__("Bid period")}
                  data={bidPeriod}
                  width={width}
              />
          }

          {showPercentsCancelled ?
            (compare ?
              <Comparison
                  translations={translations}
                  width={width}
                  state={cancelled}
                  Component={CancelledPercents}
                  title={this.__("Cancelled funding percentage")}
                  actions={actions}
              />
              :
              <CancelledPercents
                  translations={translations}
                  title={this.__("Cancelled funding percentage")}
                  actions={actions}
                  data={cancelled}
                  width={width}
              />
            ) : (compare ?
              <Comparison
                  translations={translations}
                  width={width}
                  state={cancelled}
                  Component={Cancelled}
                  title={this.__("Cancelled funding")}
              />
              :
              <Cancelled
                  translations={translations}
                  title={this.__("Cancelled funding")}
                  data={cancelled}
                  width={width}
                  actions={actions}
              />
            )
          }
        </div>
    );
  }
}