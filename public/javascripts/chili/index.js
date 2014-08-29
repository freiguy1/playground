var viewModel = new ChiliViewModel();

$(function(){
    viewModel.initialize();
    ko.applyBindings(viewModel);
});

function ChiliViewModel() {
    var self = this;
    self.loading = ko.observable(true);
    self.entries = ko.observableArray([]);
    self.initialize = function(){
        self.loading(true);
        publicAjax.getEntries().done(function(result){
            ko.mapping.fromJS(result, {}, viewModel.entries);
            self.loading(false);
        });
    };
}