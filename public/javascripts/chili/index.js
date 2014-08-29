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
            $.each(result, function(index, value) {
                if(!value.description)
                    value.description = null;
            });
            result.sort(function(left, right) {
                return left.number - right.number;
            });
            ko.mapping.fromJS(result, {}, viewModel.entries);
            self.loading(false);
        });
    };
    self.spiciness = ko.computed(function(){
        return '-chili';
    }, this);
}