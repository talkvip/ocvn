import {Store, toImmutable} from "nuclear-js";
import constants from "../actions/constants";
import keyMirror from "keymirror";
import {years, identity} from "../../tools";

var store = Store({
  getInitialState(){
    return toImmutable({
      filtersBoxOpen: false,
      tab: store.tabs.OVERVIEW,
      selectedYears: years().reduce((map, year) => map.set(year, true), toImmutable({})),
      contentWidth: 0,
      data: {},
      filters: {
      }
    })
  },

  initialize(){
    var updateDataByYear = (path, pipe = identity) => (state, {year, data}) => state.setIn(['data', path, year], pipe(data));
    var updateData = path => (state, data) => state.setIn(['data', path], data);

    this.on(constants.TAB_CHANGED, (state, tab) => state.set('tab', tab));
    this.on(constants.YEAR_TOGGLED, (state, {year, selected}) => state.setIn(['selectedYears', year], selected));
    this.on(constants.CONTENT_WIDTH_CHANGED, (state, newWidth) => state.set('contentWidth', newWidth));
    this.on(constants.COST_EFFECTIVENESS_DATA_UPDATED, updateDataByYear('costEffectiveness'));
    this.on(constants.BID_TYPE_DATA_UPDATED, updateDataByYear('bidType', toImmutable));
    this.on(constants.LOCATION_UPDATED, updateDataByYear('locations', toImmutable));
    this.on(constants.BID_PERIOD_DATA_UPDATED, updateData('bidPeriod'));
    this.on(constants.OVERVIEW_DATA_UPDATED, updateData('overview'));
    this.on(constants.CANCELLED_DATA_UPDATED, updateData('cancelled'));
    this.on(constants.FILTER_BOX_TOGGLED, (state, open) => state.set('filtersBoxOpen', open));
    this.on(constants.FILTERS_DATA_UPDATED, (state, data) => state.set('filters', toImmutable(data)));
    this.on(constants.FILTER_TOGGLED, (state, {slug, open}) => state.setIn(['filters', slug, 'open'], open));
    this.on(constants.FILTER_OPTIONS_TOGGLED, (state, {slug, option, selected}) =>
        state.setIn(['filters', slug, 'options', option, 'selected'], selected));
  }
});

store.tabs = keyMirror({
  OVERVIEW: null,
  PLANNING: null,
  TENDER_AWARD: null
});

export default store;