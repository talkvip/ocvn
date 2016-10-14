var translatable = Class => class Translatable extends Class{
  __(text){
    console.warn('__ is deprecated, use t');
    var translations = this.props.translations || {};
    return translations[text] || text;
  }

  __n(sg, pl, n){
    console.warn('__n is deprecated, use t');
    return n + " " + this.__(1 == n ? sg : pl);
  }

  t(key){
    let {translations} = this.props;
    if(!translations) console.error('Missing translations', this.constructor.name);
    if(!translations[key]) console.error('Missing translation for key', key);
    return translations[key];
  }
};

export default translatable;