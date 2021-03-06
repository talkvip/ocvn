import translatable from "../../translatable";
import Component from "../../pure-render-component";
import InputRange from "react-input-range";
import IRstyles from "react-input-range/dist/react-input-range.css";

const BILLION = 1000000000;
const MILLION = 1000000;
const THOUSAND = 1000;

let labelFormatter =  number => {
    if(typeof number == "undefined") return number;
    if(number >= BILLION) return (number/BILLION).toFixed(2) + "B";
    if(number >= MILLION) return (number/MILLION).toFixed(2) + "M";
    if(number >= THOUSAND) return (number/THOUSAND).toFixed(2) + "K";
    return number.toFixed(2);
}

class Range extends translatable(Component){
  render(){
    if(!this.state) return null;
    const {min, max} = this.state;
    const {onUpdate} = this.props;
    const minValue = this.props.minValue || min;
    const maxValue = this.props.maxValue || max;
    return (
        <section className="field">
          <header>
            {this.getTitle()}
          </header>
          <section className="options range">
            <InputRange
                minValue={min}
                maxValue={max}
                value={{min: minValue, max: maxValue}}
                onChange={(_, newVal) => onUpdate(newVal)}
                formatLabel={labelFormatter}
            />
          </section>
          <div className="range-inputs">
            {this.t('general:range:min')}
            &nbsp;
            <input
                type="number"
                className="form-control input-sm"
                value={minValue}
                onChange={e => onUpdate({min: +e.target.value, max: maxValue})}
            />
            &nbsp;
            {this.t('general:range:max')}
            &nbsp;
            <input
                type="number"
                className="form-control input-sm"
                value={maxValue}
                onChange={e => onUpdate({min: minValue, max: +e.target.value})}
            />
          </div>
        </section>
    )
  }
}

export default Range;
