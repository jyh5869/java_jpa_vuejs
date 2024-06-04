<template>
    <component :is="tag" :class="computedClasses" :role="role" aria-hidden="!hasLabelSlot">
        <span v-if="hasLabelSlot || label" class="visually-hidden">{{ label }}</span>
        <slot></slot>
        <div class="text-center"><b-spinner variant="primary" label="Text Centered"></b-spinner>{{ label }}</div>
    </component>

    <!-- 
    <div class="text-center">
        <b-spinner label="Spinning"></b-spinner>
        <b-spinner type="grow" label="Spinning"></b-spinner>
        <b-spinner variant="primary" label="Spinning"></b-spinner>
        <b-spinner variant="primary" type="grow" label="Spinning"></b-spinner>
        <b-spinner variant="success" label="Spinning"></b-spinner>
        <b-spinner variant="success" type="grow" label="Spinning"></b-spinner>
    </div>
    <div>
        <b-spinner label="Loading..."></b-spinner>
    </div>
    <div>
        <b-spinner type="grow" label="Loading..."></b-spinner>
    </div>
    <div>
        <b-button variant="primary" disabled>
            <b-spinner small></b-spinner>
            <span class="sr-only">Loading...</span>
        </b-button>

        <b-button variant="primary" disabled>
            <b-spinner small type="grow"></b-spinner>
            Loading...
        </b-button>
    </div>
    <div class="text-center">
        <b-spinner variant="primary" label="Text Centered"></b-spinner>
    </div>
     -->
</template>

<script>
import { defineComponent, computed, ref } from 'vue';

export default defineComponent({
    name: 'LoadingSpinner',
    props: {
        label: {
            type: String,
            required: false,
        },
        role: {
            type: String,
            required: false,
            default: 'status',
        },
        small: {
            type: Boolean,
            required: false,
            default: false,
        },
        tag: {
            type: String,
            required: false,
            default: 'div',
        },
        type: {
            type: String,
            required: false,
            default: 'border',
        },
        variant: {
            type: String,
            required: false,
        },
    },
    setup(props) {
        const smallBoolean = ref(props.small);
        const computedClasses = computed(() => ({
            'spinner-border': props.type === 'border' && !props.small,
            'spinner-border-sm': props.type === 'border' && props.small,
            'spinner-grow': props.type === 'grow' && !props.small,
            'spinner-grow-sm': props.type === 'grow' && props.small,
        }));

        const hasLabelSlot = computed(() => !!props.label);

        return {
            props,
            smallBoolean,
            computedClasses,
            hasLabelSlot,
        };
    },
});
</script>

<style scoped>
.visually-hidden {
    position: absolute;
    width: 1px;
    height: 1px;
    padding: 0;
    margin: -1px;
    overflow: hidden;
    clip: rect(0, 0, 0, 0);
    border: 0;
}
</style>
