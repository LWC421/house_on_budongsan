<script setup>
import { onUnmounted, watch, defineProps, ref, defineEmits, onMounted, onUpdated } from "vue";
import { useMapStore } from "@/stores";
import { storeToRefs } from "pinia";

const emit = defineEmits(["clickEvent", "setMarker"])
const mapStore = useMapStore();

const { scriptLoaded } = storeToRefs(mapStore);

const marker = ref(null);
const info = ref(null);

const props = defineProps({
  map: Object,
  lat: Number,
  lng: Number,
  click: Function,
  infoWindow: Object,
  withMarker: Boolean,
  src: String,
  size: Array,
})

onMounted(() => {
    watch(() => {
      if ( !marker.value ) {
        if (props.map) {
          if (scriptLoaded.value) {
            //스크립트가 로드 되었을 때만
            window.kakao.maps.load(() => {
              const position = new window.kakao.maps.LatLng(props.lat, props.lng);
              const options = { position };
              if (props.withMarker) {
                options.map =  props.map
              }

              if (props.src) {
                //만약 이미지에 대한 url이 있으면
                // const imageSize = new kakao.maps.Size(props.size ? props.size[0] : 32, props.size ? props.size[1] : 48)                     //주어진 사이즈가 있으면 size를 설정 없으면 기본 size로
                const imageSize = new kakao.maps.Size(32, 48)                     //주어진 사이즈가 있으면 size를 설정 없으면 기본 size로
                // const imageOption = { offset: new kakao.maps.Point(props.size ? props.size[0]/2 : 16, props.size ? props.size[1] : 48) }; //주어진 사이즈가 있으면 그거의 중앙으로 없으면 그냥
                const imageOption = { offset: new kakao.maps.Point(16, 48) }; //주어진 사이즈가 있으면 그거의 중앙으로 없으면 그냥
                const markerImage = new kakao.maps.MarkerImage(props.src, imageSize, imageOption)
                options.image = markerImage;
              }

              const initMarker = new kakao.maps.Marker(options)
              marker.value = initMarker;

              const infoWindow = new kakao.maps.InfoWindow({
                content: props.infoWindow,
                removable: true,
              })

              info.value = infoWindow;
              
              //클릭 이벤트 달아주기
              window.kakao.maps.event.addListener(initMarker, "click", () => {
                emit("clickEvent", {marker, infoWindow});
              })

              emit("setMarker", {marker: marker.value, window: infoWindow});
            })
        }
      }
    }
  })
})

//값이 변경되면 이에 따라 다시 렌더링
onUpdated(() => {
  info.value.close();

  marker.value.setMap(null);  //기존에 있던 마커는 삭제하고

  const position = new window.kakao.maps.LatLng(props.lat, props.lng);
  const options = { position };
  if (props.withMarker) {
    options.map =  props.map
  }

  if (props.src) {
    //만약 이미지에 대한 url이 있으면
    const imageSize = new kakao.maps.Size(props.size ? props.size[0] : 24, props.size ? props.size[1] : 36)                     //주어진 사이즈가 있으면 size를 설정 없으면 기본 size로
    const imageOption = { offset: new kakao.maps.Point(props.size ? props.size[0]/2 : 12, props.size ? props.size[1] : 36) }; //주어진 사이즈가 있으면 그거의 중앙으로 없으면 그냥
    const markerImage = new kakao.maps.MarkerImage(props.src, imageSize, imageOption)
    options.image = markerImage;
  }

  const initMarker = new kakao.maps.Marker(options)
  marker.value = initMarker;

  const infoWindow = new kakao.maps.InfoWindow({
    content: props.infoWindow,
    removable: true,
  })
  
  info.value = infoWindow;
  
  //클릭 이벤트 달아주기
  window.kakao.maps.event.addListener(initMarker, "click", () => {
    emit("clickEvent", {marker, infoWindow});
  })

  emit("setMarker", {marker: marker.value, window: infoWindow});
})



//데이터가 삭제될 때 마커도 삭제해주기
onUnmounted(() => {
  marker.value.setMap(null)
})

</script>


<style scoped>

</style>