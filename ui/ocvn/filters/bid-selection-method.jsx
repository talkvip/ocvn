import MultipleSelect from "../../oce/filters/inputs/multiple-select";

class BidSelectionMethod extends MultipleSelect{
  getTitle(){
    return this.t('filters:bidSelectionMethod:title');
  }

  getId(option){
    return option.get('_id');
  }

  getLabel(option){
    return option.get('_id');
  }

  transform(data){
    return data.filter(({_id}) => !!_id);
  }
}

BidSelectionMethod.ENDPOINT = 'ocds/bidSelectionMethod/all';

export default BidSelectionMethod;
