# Marquee
Marquee Views. 跑马灯效果View.

一个很方便使用和扩展的跑马灯Library，通过提供不同的 Adapter&ViewHolder 来定制不同的跑马灯View, 并且提供了常用类型的跑马灯效果：MarqueeTextView/TextMarqueeView.

NOTE: MarqueeTextView is a TextView, which extends AppCompatTextView, but TextMarqueeView is a MarqueeView, which supports text marquee only.

###Effect/效果图
---

<img src="/media/marquee.gif"/>

### 属性
MarqueeTextView属性

| Attribute/属性          | Description/描述 |
|:---				     |:---|
| repeat_mode         |    repeat mode, e.g. forever/once/重复模式, 可选forever/once.       |
| forever_mode         | forever mode, e.g. shortStay/appending/永久跑马时, 一次跑马结束后是延时还是立即开始下一轮跑马.            |
| short_stay_delay         |  /永久跑马时, 延时时间开始下一轮.          |
| start_delay | start delay/ 开启跑马时延时多久开始. |
| marquee_speed | marquee speed/ 一次跑马完成耗时多久. |
| reset_x | reset x when text changed/文字重复设置时是否重新开启跑马. |
| tap_to_pause | tap to pause/ 是否点击开启/暂停跑马. |

MarqueeView属性

| Attribute/属性          | Description/描述 |
|:---				     |:---|
| marqueeDirection         |    marquee direction, including startToEnd/topToBottom/endToStart/bottomToTop 跑马灯方向.      |
| marqueeAnimDuration         | animation duration/动画执行时间.           |

TextMarqueeView属性

| Attribute/属性          | Description/描述 |
|:---				     |:---|
| marquee_direction         |    marquee direction/跑马灯方向.       |
| marquee_anim_duration         | animation duration/动画执行时间.            |
| marquee_text_gravity         |  text gravity/文字位置.          |
| marquee_text_color | text color/文字颜色. |
| marquee_text_size | text size/文字大小. |
| marquee_single_line | single line/文字是否单行显示. |
| marquee_text_font | text font/文字字体. |

###How to use/使用说明


<br>MarqueeTextView
```
    <me.bytebeats.views.marquee.MarqueeTextView
        android:id="@+id/marquee_text_view_long"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="30dp"
        android:layout_marginBottom="30dp"
        android:text="@string/marquee_text_view_text"
        android:textAllCaps="true"
        android:textSize="25sp"
        app:forever_mode="appending"
        app:marquee_speed="5000"
        app:repeat_mode="forever"
        app:reset_x="true"
        app:short_stay_delay="2000"
        app:start_delay="1000"
        app:tap_to_pause="true" />

```
<br>MarqueeTextView APIs:
```
        var short = true
        binding.marqueeTextViewShort.setOnClickListener {
            short = !short
            if (short) {
                binding.marqueeTextViewShort.setText(R.string.marquee_text_view_text)
            } else {
                binding.marqueeTextViewShort.setText(R.string.marquee_text_view_text_short)
            }
            Log.i(TAG, "short: $short")
        }
        binding.marqueeTextViewLong.setOnClickListener { Log.i(TAG, "click") }
        binding.btnResume.setOnClickListener {
            binding.marqueeTextViewLong.resumeMarquee()
            binding.marqueeTextViewShort.resumeMarquee()
        }
        binding.btnPause.setOnClickListener {
            binding.marqueeTextViewLong.pauseMarquee()
            binding.marqueeTextViewShort.pauseMarquee()
        }
```
<br>MarqueeTextView
```
    <me.bytebeats.views.marquee.TextMarqueeView
        android:id="@+id/marquee_text"
        android:layout_width="match_parent"
        android:layout_height="50dp"
        android:autoStart="false"
        android:flipInterval="3000"
        app:marquee_anim_duration="500"
        app:marquee_direction="endToStart"
        app:marquee_single_line="false"
        app:marquee_text_color="#009933"
        app:marquee_text_size="20sp" />

```
<br>MarqueeTextView APIs:
```
        private const val TEXT = "赵客缦胡缨，吴钩霜雪明。" +
                "银鞍照白马，飒沓如流星。" +
                "十步杀一人，千里不留行。" +
                "事了拂衣去，深藏身与名。" +
                "闲过信陵饮，脱剑膝前横。" +
                "将炙啖朱亥，持觞劝侯嬴。" +
                "三杯吐然诺，五岳倒为轻。" +
                "眼花耳热后，意气素霓生。" +
                "救赵挥金槌，邯郸先震惊。" +
                "千秋二壮士，烜赫大梁城。" +
                "纵死侠骨香，不惭世上英。" +
                "谁能书阁下，白首太玄经。"
                ......
        binding.marqueeText.onItemClickListener = object : OnItemClickListener<TextMarqueeView> {
            override fun onItemClick(view: TextMarqueeView, itemView: View, position: Int) {
                Log.i(TAG, "position: $position")
            }
        }
        binding.marqueeText.startWithMessage(TEXT)
        binding.marqueeText2.startWithMessage(TEXT)
```
<br>When MarqueeTextView works with ListView/RecyclerView/GridView/..., in the `Adapter`:
```
        override fun onViewAttachedToWindow(holder: MarqueeTextViewHolder) {
            super.onViewAttachedToWindow(holder)
            holder.textMarqueeView.startFlipping()
        }

        override fun onViewDetachedFromWindow(holder: MarqueeTextViewHolder) {
            super.onViewDetachedFromWindow(holder)
            holder.textMarqueeView.stopFlipping()
        }
```
<br>MarqueeView
```
    <me.bytebeats.views.marquee.MarqueeView
        android:id="@+id/marquee_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="center_vertical"
        android:layout_marginStart="8dp"
        android:layout_marginTop="8dp"
        android:layout_marginEnd="8dp"
        android:autoStart="false"
        android:flipInterval="2000"
        app:marqueeAnimDuration="400"
        app:marqueeDirection="startToEnd" />

```
<br>MarqueeView APIs: 
```
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        marqueeViewModel =
            ViewModelProvider(this).get(MarqueeViewModel::class.java)

        _binding = FragmentMarqueeViewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.marqueeView.adapter = adapter

        return root
    }

    override fun onStart() {
        super.onStart()
        val triples = mutableListOf<Triple<Int, String, String>>()
        for (i in 0 until 10) {
            val first = when (i % 3) {
                0 -> R.color.purple_200
                1 -> R.color.purple_500
                else -> R.color.purple_700
            }
            triples.add(Triple(first, "Title $i", "Subtitle $i"))
        }
        adapter.update(triples)
    }

    override fun onResume() {
        super.onResume()
        binding.marqueeView.startFlipping()
    }

    override fun onPause() {
        super.onPause()
        binding.marqueeView.stopFlipping()
    }

    private class MarqueeAdapter(val context: Context) :
        MarqueeView.Adapter<MarqueeAdapter.MarqueeViewHolder>() {
        private val triples = mutableListOf<Triple<Int, String, String>>()

        fun update(data: Collection<Triple<Int, String, String>>) {
            triples.clear()
            triples.addAll(data)
            notifyDataSetChanged()
        }

        override fun createViewHolder(position: Int): MarqueeViewHolder {
            return MarqueeViewHolder(
                LayoutInflater.from(context).inflate(R.layout.list_item_marquee_layout, null)
            )
        }

        override fun bindViewHolder(holder: MarqueeView.ViewHolder, position: Int) {
            (holder as MarqueeViewHolder).bind(position)
        }

        override fun itemCount(): Int = triples.size

        fun item(position: Int): Triple<Int, String, String> = triples[position]

        inner class MarqueeViewHolder(view: View) : MarqueeView.ViewHolder(view) {
            private val image = view.findViewById<ImageView>(R.id.image_view)
            private val title = view.findViewById<TextView>(R.id.title)
            private val subtitle = view.findViewById<TextView>(R.id.subtitle)

            fun bind(position: Int) {
                item(position).apply {
                    image.setImageResource(first)
                    title.text = second
                    subtitle.text = third
                    Log.i("MarqueeView", this.toString())
                }
            }
        }
    }
```

Note: MarqueeView provides Adapter&ViewHolder to support marquee view groups.

### To-Dos

publish marquee-library to mavenCentral.