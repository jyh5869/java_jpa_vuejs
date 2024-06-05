<template>
    <div v-if="showSpinner == true" class="spinner-wrap clearfix m-3">
        <div><b-spinner large :variant="spinnerDataProp.variant" :label="spinnerDataProp.label" :type="spinnerDataProp.type"></b-spinner></div>
        <div>{{ spinnerDataProp.label }}</div>
    </div>
</template>

<script>
import { defineComponent } from 'vue';

export default defineComponent({
    name: 'LoadingSpinner',
    props: {
        showSpinnerProp: {
            type: Boolean,
            default: false,
        },
        spinnerDataProp: {
            type: Object,
            default: () => ({
                label: '..Loading11..',
                type: 'border',
                variant: 'primary', // 토스트 배경 색상
            }),
            validator(value) {
                return typeof value.label == 'string' && typeof value.type == 'string' && typeof value.variant == 'number';
            },
        },
    },
    data(props) {
        return {
            showSpinner: props.showSpinnerProp,
            spinnerData: props.spinnerDataProp,
        };
    },
});
</script>

<style scoped>
.spinner-wrap {
    position: relative;
    top: 250px;
}
</style>
