<template>

<v-data-table
    :headers="headers"
    :items="myPage"
    :items-per-page="5"
    class="elevation-1"
  ></v-data-table>

</template>

<script>
  const axios = require('axios').default;

  export default {
    name: 'MyPage',
    props: {
      value: Object,
      editMode: Boolean,
      isNew: Boolean
    },
    data: () => ({
        headers: [
            { text: "id", value: "id" },
            { text: "name", value: "name" },
            { text: "addr", value: "addr" },
            { text: "grade", value: "grade" },
            { text: "win", value: "win" },
            { text: "loss", value: "loss" },
            { text: "drawId", value: "drawId" },
            { text: "drawDate", value: "drawDate" },
            { text: "drawResult", value: "drawResult" },
        ],
        myPage : [],
    }),
    async created() {
      var temp = await axios.get(axios.backend + '/mypages')

      this.myPage = temp.data._embedded.mypages;

    },
    methods: {
    }
  }
</script>

